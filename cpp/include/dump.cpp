#include <vector>
#include <string>
#include <sstream>
#include <iomanip>

void dump_char_array(const char *array, size_t size)
{
    std::cout << "[" << size << "] ";
    for (size_t i = 0; i < size; ++i)
    {
        // Print each byte in hexadecimal format
        std::cout << std::hex << std::setw(2) << std::setfill('0') << static_cast<int>(static_cast<unsigned char>(array[i])) << ' ';

        // Optionally, print a newline every 16 bytes for better readability
        /*
        if ((i + 1) % 16 == 0)
        {
            std::cout << '\n';
        }
        */
    }
    std::cout << std::dec << std::endl; // Reset to decimal format
}