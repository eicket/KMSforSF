#include <iostream>
#include "base64_decoder.cpp"

int main()
{
    // Example Base64-encoded string
    std::string base64_string = "SGVsbG8gd29ybGQ=";

    // Decode Base64 to binary
    std::vector<unsigned char> binary_data = base64_decode(base64_string);

    // Output binary data as characters (for readability)
    std::cout << "Decoded binary data: ";
    for (unsigned char byte : binary_data)
    {
        std::cout << byte;
    }
    std::cout << std::endl;

    return 0;
}
