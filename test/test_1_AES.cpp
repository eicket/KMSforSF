#include <iostream>
#include <cstring> // for memcpy, memset
// #include <miracl/aes.cpp> // Include the MIRACL AES header

#include "miracl/aes.cpp"
using namespace core;
#include "include/dump.cpp"

// g++ test_1_AES.cpp miracl/core.a -o test_1_AES.exe

int main()
{
    // AES key (16 bytes for AES-128, 24 bytes for AES-192, 32 bytes for AES-256)
    // char key[] = "0123456789abcdef"; // 16-byte key (AES-128)

     std::string password = "o3iR0WM8I";

    // make a 256 bit hash of the strong password, and use this as the encryption key
    char hashResult[32];
    hash256 sh256;
    HASH256_init(&sh256);
    for (int i = 0; i < password.size(); i++)
        HASH256_process(&sh256, password[i]);
    HASH256_hash(&sh256, hashResult);

    std::cout << "Hash result : ";
    dump_char_array(hashResult, 32);


    // no padding is needed
    char plaintext[1000];
    plaintext[0] = '1';
    for (int i = 1; i < 999; i++)
    {
        plaintext[i] = 'x';
    }
    plaintext[999] = '2';
    std::cout << "Plain  : ";
    dump_char_array(plaintext, sizeof(plaintext));

    // AES structure
    aes a;

    //  int core::AES_init(core::aes* a, int mode, int nk, char *key, char *iv)
    // Initialize the AES encryption structure for CBC mode (IV is set to zero)
    // 16 key length is ok, 32 
    AES_init(&a, ECB, 32, hashResult, NULL); // NULL IV means IV = 0

    // Encrypt the plaintext
    // unsign32 core::AES_encrypt(core::aes* a, char *buff)
    AES_encrypt(&a, plaintext);

    // End the AES encryption session
    AES_end(&a);

    std::cout << "Encrypted  : ";
    dump_char_array(plaintext, sizeof(plaintext));

    // Initialize AES again for decryption
    AES_init(&a, ECB, 32, hashResult, NULL); // Use the same key and IV (IV = 0)

    // Decrypt the ciphertext
    // unsign32 core::AES_decrypt(core::aes *a, char *buff)
    AES_decrypt(&a, plaintext);

    // End the AES decryption session
    AES_end(&a);

    std::cout << "Decrypted  : ";
    dump_char_array(plaintext, sizeof(plaintext));

    return 0;
}
