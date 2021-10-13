#!/bin/bash

TOKEN=$1
BASE_URL=${2:-http://localhost:8080}

read -r -d '' XML_REQUEST << EOM
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns="http://luin.dev/digikoppeling/gb/server/1.0">
   <soapenv:Header/>
   <soapenv:Body>
      <ns:getExternalDataReference>
         <path>${TOKEN}</path>
      </ns:getExternalDataReference>
   </soapenv:Body>
</soapenv:Envelope>
EOM

# Create temporary files
REQUEST_BODY=$(mktemp)
RESPONSE_BODY=$(mktemp)

echo "$XML_REQUEST"  > ${REQUEST_BODY}

curl -o ${RESPONSE_BODY} \
    -s ${BASE_URL}/service/gb \
    -H 'Content-Type: text/xml;charset=UTF-8' \
    -H 'MIME-Version: 1.0' \
    -H 'SOAPAction: ""' \
    --data-binary @${REQUEST_BODY} \

SENDER_URL=`cat ${RESPONSE_BODY} | sed "s/.*<.*:senderUrl .*>\(.*\)<\/.*:senderUrl>.*/\1/"`
echo $SENDER_URL
#FILESIZE=`cat ${RESPONSE_BODY} | sed "s/.*<.*:size>\(.*\)<\/.*:size>.*/\1/p"`
#echo $FILESIZE
#MD5SUM=`cat ${RESPONSE_BODY} | sed "s/.*<.*:checksum type=\"MD5\">\(.*\)<\/.*:checksum>.*/\1/p"`
#echo $MD5SUM

# Remove the temporary file.
rm ${REQUEST_BODY}
rm ${RESPONSE_BODY}
