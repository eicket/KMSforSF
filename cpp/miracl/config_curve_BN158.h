/*
 * Copyright (c) 2012-2020 MIRACL UK Ltd.
 *
 * This file is part of MIRACL Core
 * (see https://github.com/miracl/core).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef CONFIG_CURVE_BN158_H
#define CONFIG_CURVE_BN158_H

#include"core.h"
#include"config_field_BN158.h"

// ECP stuff

#define CURVETYPE_BN158 WEIERSTRASS
#define CURVE_A_BN158 0
#define PAIRING_FRIENDLY_BN158 BN_CURVE
#define CURVE_SECURITY_BN158 128
#define HTC_ISO_BN158 0

// Permit alternate compression method if 3 spare top bits in field representation
// Must be set manually
// #define ALLOW_ALT_COMPRESS_BN158

#if PAIRING_FRIENDLY_BN158 != NOT_PF

#define HTC_ISO_G2_BN158 0

#define USE_GLV_BN158   /**< Note this method is patented (GLV), so maybe you want to comment this out */
#define USE_GS_G2_BN158 /**< Well we didn't patent it :) But may be covered by GLV patent :( */
#define USE_GS_GT_BN158 /**< Not patented, so probably safe to always use this */

#define POSITIVEX 0
#define NEGATIVEX 1

#define SEXTIC_TWIST_BN158 M_TYPE
#define SIGN_OF_X_BN158 NEGATIVEX

#define ATE_BITS_BN158 42
#define G2_TABLE_BN158 49

#endif


#if CURVE_SECURITY_BN158 == 128
#define AESKEY_BN158 16 /**< Symmetric Key size - 128 bits */
#define HASH_TYPE_BN158 SHA256  /**< Hash type */
#endif

#if CURVE_SECURITY_BN158 == 192
#define AESKEY_BN158 24 /**< Symmetric Key size - 192 bits */
#define HASH_TYPE_BN158 SHA384  /**< Hash type */
#endif

#if CURVE_SECURITY_BN158 == 256
#define AESKEY_BN158 32 /**< Symmetric Key size - 256 bits */
#define HASH_TYPE_BN158 SHA512  /**< Hash type */
#endif


namespace BN158_BIG = B160_56;
namespace BN158_FP = BN158;

#endif
