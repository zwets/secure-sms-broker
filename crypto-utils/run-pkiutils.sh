#!/bin/sh

BASE="$(dirname "$0")/target"
exec java -cp $BASE/crypto-utils-0.9.1.jar:$BASE/lib:$BASE/classes it.zwets.sms.crypto.PkiUtils "$@"
