#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// uncomment for tracing aes.cpp
// #include "miracl/aes.cpp"

#include "miracl/bls_BN158.h"
using namespace BN158;
#include "miracl/randapi.h"
using namespace core;
#include "miracl/core.h"

#include <iostream>
#include <unistd.h>
#include <fstream>
// from https://github.com/nlohmann/json/releases/tag/v3.11.3
#include "include/json.hpp"
using json = nlohmann::json;

#include "include/base64_decoder.cpp"
#include "include/dump.cpp"

std::string privateKeyFile;
std::string password;
std::string signatureFile;
char *message;

/*
g++ sign.cpp miracl/core.a -o sign.exe
sign -m "240817_081230|1000|CQ K1ABC EM20" -k privateKeyFile_1000.enc -p xpru8HQsB -s signature1.bin
sign -m "240817_081230|9|CQ K1ABC EM20" -k privateKeyFile_9.json -s signature2.bin
*/

int main(int argc, char *argv[])
{
    int opt;
    while ((opt = getopt(argc, argv, "m:k:p:s:")) != -1)
    {
        switch (opt)
        {
        case 'm':
            std::cout << "Message: " << optarg << std::endl;
            message = optarg;
            break;
        case 'k':
            std::cout << "Private key file: " << optarg << std::endl;
            privateKeyFile = optarg;
            break;
        case 'p':
            std::cout << "Password: " << optarg << std::endl;
            password = optarg;
            break;
        case 's':
            std::cout << "Signature file: " << optarg << std::endl;
            signatureFile = optarg;
            break;
        default:
            std::cerr << "Usage: " << argv[0] << " [-k filename] [-p password] [-s filename]" << std::endl;
            return 1;
        }
    }

    printf("%d bit build\n", CHUNK);

    std::ifstream file(privateKeyFile, std::ios::binary);
    if (!file.is_open())
    {
        std::cerr << "Could not open the private key file!" << std::endl;
        return 1;
    }

    json jsonData;

    // decrypt the private key file extension is .enc
    std::string pattern = ".enc";
    if (privateKeyFile.compare(privateKeyFile.length() - pattern.length(), pattern.length(), pattern) == 0)
    {
        // is an encrypted file
        std::cout << "Encrypted private key file!" << std::endl;

        // make a 256 bit hash of the strong password, and use this as the encryption key
        char hashResult[32];
        hash256 sh256;
        HASH256_init(&sh256);
        for (int i = 0; i < password.size(); i++)
            HASH256_process(&sh256, password[i]);

        HASH256_hash(&sh256, hashResult);
        octet K = {32, 32, hashResult};

        std::cout << "Hash result : ";
        dump_char_array(hashResult, 32);

        // read the encrypted file - is in base64, so readable chars
        file.seekg(0, std::ios::end);   // Move the cursor to the end
        size_t fileSize = file.tellg(); // Get the file size
        file.seekg(0, std::ios::beg);   // Move the cursor back to the beginning

        char *encodedFileContents = new char[fileSize];
        file.read(encodedFileContents, fileSize);

        std::cout << "Encrypted private key file (encoded): ";
        dump_char_array(encodedFileContents, fileSize);

        std::vector<unsigned char> decodedFileContents = base64_decode(encodedFileContents);

        int size = decodedFileContents.size();
        char encrypted[size];
        std::memcpy(encrypted, decodedFileContents.data(), size);
        std::cout << "Encrypted private key (decoded): ";
        dump_char_array(encrypted, size);

        octet C = {size, size, encrypted};
        // std::cout << "Encrypted length C.len : " << C.len << std::endl;

        char decrypted[size];
        octet PLAIN = {0, size, decrypted};

        int result = AES_CBC_IV0_DECRYPT(&K, &C, &PLAIN);
        if (result != 1)
        {
            std::cerr << "Error decrypting!" << std::endl;
            // return -1;
        };

        // std::cout << "Decrypted length : " << PLAIN.len << std::endl;
        std::cout << "Decrypted private key file: ";
        dump_char_array(decrypted, sizeof(decrypted));

        // OCT_output_string(&PLAIN);

        char plainText[size];
        OCT_toStr(&PLAIN, plainText);

        std::string jsonString(plainText);

        // Parse the string into a JSON object
        jsonData = nlohmann::json::parse(jsonString);
    }
    else
    {
        file >> jsonData;
    }

    file.close();

    //std::cout << "Dump: " << jsonData.dump() << std::endl;

    /*
    // Iterate over the JSON elements
    for (auto &[key, value] : jsonData.items())
    {
        std::cout << "Key: " << key << std::endl;
        std::cout << "Value: " << value << std::endl;

        // If the value is an object or array, you can iterate over it as well
        if (value.is_object())
        {
            for (auto &[subKey, subValue] : value.items())
            {
                std::cout << "  Subkey: " << subKey << ", Subvalue: " << subValue << std::endl;
            }
        }
    }
    */

    std::string encodedPrivateKey;
    std::int32_t indexInJson;

    // Check if the top-level element is an array
    if (jsonData.is_array())
    {
        for (const auto &item : jsonData)
        {
            if (item.contains("privateKey"))
            {
                encodedPrivateKey = item["privateKey"];
                std::cout << "encodedPrivateKey: " << encodedPrivateKey << std::endl;
                std::int32_t indexInJson = item["index"].get<std::int32_t>();
                std::cout << "index in json: " << indexInJson << std::endl;
            }
            break;
        }
    }
    else
    {
        std::cout << "The JSON data is not an array !" << std::endl;
    }

    // do a sanity check : suffix in filename == index in json == index in clear message

    std::vector<unsigned char> privateKey = base64_decode(encodedPrivateKey);

    int size = privateKey.size();
    char s[size];
    std::memcpy(s, privateKey.data(), size);

    std::cout << "Private key : ";
    dump_char_array(s, size);

    char w[4 * BFS_BN158 + 1], sig[BFS_BN158 + 1]; // w is 4* if not compressed else 2*. sig is 2* if not compressed, else 1*

    octet S = {(int)sizeof(s), (int)sizeof(s), s};
    octet SIG = {0, sizeof(sig), sig};
    octet M = {sizeof(message), sizeof(message), message};

    // OCT_jstring(&M, message);

    int res = BLS_INIT();
    if (res == BLS_FAIL)
    {
        std::cerr << "Failed to initialize" << std::endl;
        return res;
    }

    BLS_CORE_SIGN(&SIG, &M, &S);

    std::cout << "Signature   : ";
    dump_char_array(sig, sizeof(sig));

    std::ofstream sigFile(signatureFile, std::ios::binary);
    if (!sigFile)
    {
        std::cerr << "Error opening signature file!" << std::endl;
        return 1;
    }
    for (size_t i = 0; i < sizeof(sig); i++)
    {
        sigFile << sig[i];
    }
    sigFile.close();
}
