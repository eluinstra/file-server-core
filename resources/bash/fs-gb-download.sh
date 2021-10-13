GEN_OUTPUTFILE=$(echo download_`date +%Y-%m-%d_%H-%M`.dump)
OUTPUT_FILE=${2:-$GEN_OUTPUTFILE}
GB_URL=$1

curl --cert-type P12 \
  --cert keystore.p12:password \
  --insecure \
  -o $OUTPUT_FILE \
  -s $GB_URL
