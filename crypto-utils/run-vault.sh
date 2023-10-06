#!/bin/sh

BASE="$(dirname "$0")/target"
exec java -cp $BASE/crypto-utils-0.9.1.jar:$BASE/lib/slf4j-reload4j-2.0.7.jar:$BASE/lib/reload4j-1.2.22.jar:$BASE/lib:$BASE/classes it.zwets.sms.crypto.Vault "$@"
