#!/bin/bash

ALIAS=${1:-localhost}
BASE_URL=${2:-http://localhost:8080}

CERT=$(keytool -keystore keystore.p12 -exportcert -alias $ALIAS -storepass password | base64)

read -r -d '' XML_REQUEST << EOM
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns="http://luin.dev/file/server/1.0">
   <soapenv:Header/>
   <soapenv:Body>
      <ns:createUser>
         <user>
            <name>${ALIAS}</name>
            <certificate>${CERT}</certificate>
         </user>
      </ns:createUser>
   </soapenv:Body>
</soapenv:Envelope>
EOM

# Create temporary files
REQUEST_BODY=$(mktemp)
RESPONSE_BODY=$(mktemp)

echo "$XML_REQUEST" > ${REQUEST_BODY}

curl -o ${RESPONSE_BODY} \
    -s ${BASE_URL}/service/user \
    -H 'Content-Type: text/xml;charset=UTF-8' \
    -H 'MIME-Version: 1.0' \
    -H 'SOAPAction: ""' \
    --data-binary @${REQUEST_BODY} \

USER_ID=`cat ${RESPONSE_BODY} | sed "s/.*<id>\(.*\)<\/id>.*/\1/"`
echo $USER_ID

# Remove the temporary file.
rm ${REQUEST_BODY}
rm ${RESPONSE_BODY}
