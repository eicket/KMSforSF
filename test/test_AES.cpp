#include <iostream>
#include "miracl/aes.cpp"
using namespace core;
#include "miracl/core.h"
using namespace core;
#include "include/dump.cpp"

// g++ test_AES.cpp miracl/core.a -o test_AES.exe

int main()
{
   // char message[] = "240817_081230|8|CQ K1ABC EM20XX";
    char message[1025];
     for (int i = 0; i < 1025; i++)
    {
        message[i] = 'x';
    }


    std::cout << "Message : ";
    dump_char_array(message, sizeof(message));

// string is 29 chars, size is 30
    int size = sizeof(message);

    std::string password = "o3iR0WM8I";
    char key[] = "0123456789abcdef"; // 16-byte key (AES-128)

    // make a 256 bit hash of the strong password, and use this as the encryption key
    char hashResult[32];
    hash256 sh256;
    HASH256_init(&sh256);
    for (int i = 0; i < password.size(); i++)
        HASH256_process(&sh256, password[i]);
    HASH256_hash(&sh256, hashResult);

    std::cout << "Hash result : ";
    dump_char_array(hashResult, 32);

    char encrypted[size];

    octet K = {0, 16, hashResult};
    OCT_jstring(&K, hashResult);

    octet P = {0, size, message};
    OCT_jstring(&P, message);

    // OCT_output_string(&P);

    octet C = {0, size, encrypted};
    // see aes.cpp
    AES_CBC_IV0_ENCRYPT(&K, &P, &C);

    std::cout << "Encrypted length : " << C.len << std::endl;
    std::cout << "Encrypted : ";
    dump_char_array(encrypted, size);





 std::cout << "Size : " << size << std::endl;
   
    // key , encrypted , message
    // octet ENC = {0, size, encrypted};
    // OCT_jstring(&ENC, encrypted);
 
    char decrypted[size];
    octet PLAIN = {0, size, decrypted};

      std::cout << "Encrypted length : " << C.len << std::endl;

    int result = AES_CBC_IV0_DECRYPT(&K, &C, &PLAIN);
    if (result != 0)
    {
        std::cerr << "Error decrypting!" << std::endl;
       // return -1;
    };

    std::cout << "Decrypted length : " << PLAIN.len << std::endl;
    std::cout << "Decrypted : ";
    dump_char_array(decrypted, sizeof(decrypted));

    //std::cout << decrypted << std::endl;

    return 0;
}
