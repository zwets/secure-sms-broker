#!/bin/bash

export LC_ALL="C"
set -euo pipefail
cd "$(dirname "$0")"

KEYSTORE=vault.keystore
ALIAS=vault.alias
STOREPASS=123456

# Create KEYSTORE if doesn't exist
[ -f $KEYSTORE ] || keytool -genkeypair -keyalg RSA -keysize 2048 -validity 36500 -storepass $STOREPASS -keystore $KEYSTORE -alias $ALIAS -dname CN=$ALIAS

# To extract the certificate and public key (note: ./run-vault.sh $VAULT pubkey $ALIAS also yields the pubkey)
#keytool -exportcert -keystore $KEYSTORE -storepass $STOREPASS -alias $ALIAS | tee $ALIAS.x509 |
#  openssl x509 -noout -pubkey | tee $ALIAS.pub |
#  openssl rsa -RSAPublicKey_in -outform DER -pubout -out $ALIAS.der
 
