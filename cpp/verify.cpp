// Erik Icket, ON4PB - 2024
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "miracl/bls_BN158.h"
using namespace BN158;
#include "miracl/randapi.h"
using namespace core;

#include <iostream>
#include <unistd.h>
#include <fstream>
#include "include/json.hpp" // from https://github.com/nlohmann/json/releases/tag/v3.11.3
using json = nlohmann::json;

#include "include/base64_decoder.cpp"
#include "include/dump.cpp"

// g++ verify.cpp miracl/core.a -o ver.exe
// ver.exe -m "240817_081230|1000|CQ K1ABC EM20" -k publicKeyFile.json -s signature1.bin
// ver.exe -m "240817_081230|9|CQ K1ABC EM20" -k publicKeyFile.json -s signature2.bin

// char message[] = "240817_081230|1000|CQ K1ABC EM20";
std::string publicKeyFile;
std::string signatureFile;
char *message;

int main(int argc, char *argv[])
{
    int opt;
    while ((opt = getopt(argc, argv, "m:k:s:")) != -1)
    {
        switch (opt)
        {
        case 'm':
            std::cout << "Message: " << optarg << std::endl;
            message = optarg;
            break;
        case 'k':
            std::cout << "Public key file: " << optarg << std::endl;
            publicKeyFile = optarg;
            break;
        case 's':
            std::cout << "Signature file: " << optarg << std::endl;
            signatureFile = optarg;
            break;
        default:
            std::cerr << "Usage: " << argv[0] << "[-s filename]" << std::endl;
            return 1;
        }
    }

    printf("%d bit build\n", CHUNK);

    std::ifstream file("publicKeyFile.json");
    if (!file.is_open())
    {
        std::cerr << "Could not open the file !" << std::endl;
        return 1;
    }

    json jsonData;
    file >> jsonData;
    file.close();

    // Iterate over the JSON elements
    /*
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

    // parse the message and extract the key index
    char *firstPipe = strchr(message, '|');
    char *secondPipe = strchr(firstPipe + 1, '|');
    int length = secondPipe - firstPipe - 1;
    // add 1 for the \0 char
    char index[length + 1];
    strncpy(index, firstPipe + 1, length);
    index[length] = '\0';

    // std::cout << "Index : ";
    // dump_char_array(index, sizeof(index));

    // This constructor automatically determines the length of the char array based on the null-terminator ('\0').
    std::string sIndex(index);
    // std::cout << "sIndex : " << sIndex << ", len : " << sIndex.size() << std::endl;
    std::string encodedPublicKey;

    // Check if the top-level element is an array
    if (jsonData.is_array())
    {
        for (const auto &item : jsonData)
        {
            // std::cout << "check item : " << item["index"] << ", len : " << item["index"].size() << std::endl;
            std::int32_t value = item["index"].get<std::int32_t>();
            // std::cout << "value : " << value << std::endl;

            // value is an int !!
            if (value == std::stoi(sIndex))
            {
                std::cout << "Found index : " << sIndex << std::endl;

                encodedPublicKey = item["publicKey"];
                std::cout << "encodedPublicKey: " << encodedPublicKey << std::endl;
                break;
            }
        }
    }
    else
    {
        std::cout << "The JSON data is not an array!" << std::endl;
    }

    if (encodedPublicKey.empty())
    {
        std::cerr << "Key index not found in public key file !" << std::endl;
        return 1;
    }

    std::vector<unsigned char> publicKey = base64_decode(encodedPublicKey);

    char w[4 * BFS_BN158 + 1]; // w is 4* if not compressed else 2*.
    std::memcpy(w, publicKey.data(), publicKey.size());

    std::cout << "Public key : ";
    dump_char_array(w, publicKey.size());
    octet W = {sizeof(w), sizeof(w), w};

    // read the signature file in binary mode

    std::ifstream file1(signatureFile, std::ios::binary);
    if (!file1.is_open())
    {
        std::cerr << "Could not open the signature file!" << std::endl;
        return 1;
    }

    file1.seekg(0, std::ios::end);   // Move the cursor to the end
    size_t fileSize = file1.tellg(); // Get the file size
    file1.seekg(0, std::ios::beg);   // Move the cursor back to the beginning

    std::cout << "File size :" << fileSize << std::endl;

    char sig[BFS_BN158 + 1]; // sig is 2* if not compressed, else 1*

    file1.read(sig, fileSize);

    file1.close();

    std::cout << "Signature on file  : ";
    dump_char_array(sig, sizeof(sig));

    octet SIG = {sizeof(sig), sizeof(sig), sig};

    octet M = {sizeof(message), sizeof(message), message};
    // OCT_jstring(&M, message);

    int res = BLS_INIT();
    if (res == BLS_FAIL)
    {
        std::cerr << "Failed to initialize" << std::endl;
        return res;
    }

    res = BLS_CORE_VERIFY(&SIG, &M, &W);
    if (res == BLS_OK)
        std::cout << "Signature is OK" << std::endl;
    else
        std::cout << "Signature is *NOT* OK" << std::endl;
}
