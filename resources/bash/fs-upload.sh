#!/bin/bash

FILE_NAME=$1
BASE_URL=${2:-http://localhost:8080}

# Prepare the headers for the XML request body
read -r -d '' REQUEST_HEADERS << EOM
----=_Part_4_1959909680.1544697065790
Content-Type: text/xml; charset=UTF-8
Content-ID: 0968015446
EOM

# Prepare the XML request body itself
read -r -d '' XML_REQUEST << EOM
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
  <SOAP-ENV:Header />
  <SOAP-ENV:Body>
      <ns:uploadFile xmlns:ns="http://luin.dev/file/server/1.0">
         <userId>0</userId>
         <file>
            <content><inc:Include href="cid:962538495782" xmlns:inc="http://www.w3.org/2004/08/xop/include"/></content>
         </file>
      </ns:uploadFile>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
EOM

# Prepare the headers for the attachment
read -r -d '' FILE_HEADERS << EOM
----=_Part_4_1959909680.1544697065790

Content-Type: application/octet-stream
Content-Transfer-Encoding: binary
Content-ID: <962538495782>
Content-Disposition: attachment; name="${FILE_NAME}"; filename="${FILE_NAME}"
EOM

# Prepare the terminating line
read -r -d '' TERMINATOR << EOM
----=_Part_4_1959909680.1544697065790--
EOM

# Create temporary files
REQUEST_BODY=$(mktemp)
RESPONSE_BODY=$(mktemp)

# Stitch the request body together by concatenating all parts in the right order.
echo "$REQUEST_HEADERS" >> ${REQUEST_BODY}
# We use ANSI-C quoting for enforcing newlines: https://stackoverflow.com/a/5295906/1523342
echo $'\r\n\r\n'        >> ${REQUEST_BODY}
echo "$XML_REQUEST"     >> ${REQUEST_BODY}
echo $'\r\n'            >> ${REQUEST_BODY}
echo "$FILE_HEADERS"    >> ${REQUEST_BODY}
#echo $'\r\n\r\n'        >> ${REQUEST_BODY}
echo $'\r'            >> ${REQUEST_BODY}
 cat ${FILE_NAME}       >> ${REQUEST_BODY}
echo "$TERMINATOR"      >> ${REQUEST_BODY}

curl -o ${RESPONSE_BODY} \
    -s ${BASE_URL}/service/file \
    -H 'Content-Type: multipart/related; type="text/xml"; start="0968015446"; boundary="--=_Part_4_1959909680.1544697065790"' \
    -H 'MIME-Version: 1.0' \
    -H 'SOAPAction: ""' \
    --data-binary @${REQUEST_BODY} \

TOKEN=`cat ${RESPONSE_BODY} | grep soap | sed "s/.*<path>\(.*\)<\/path>.*/\1/"`
echo $TOKEN

# Remove the temporary file.
rm ${REQUEST_BODY}
rm ${RESPONSE_BODY}
