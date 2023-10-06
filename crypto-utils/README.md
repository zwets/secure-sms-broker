## Testing the Vault

@TODO@ clean up

```bash
# Generates vault.keystore with one key with alias vault.alias
./make-keystore.sh

# Extract the public key for the vault.alias
./run-vault.sh vault.keystore pubkey vault.alias >vault.der

# Encrypt and decrypt
echo "Hello World" | ./run-pkiutils.sh encrypt vault.der | ./run-vault.sh vault.keystore decrypt vault.alias
./run-vaule

```
