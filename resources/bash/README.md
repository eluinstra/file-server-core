# bash scripts for basic SOAP integration

These scripts are used for large file uploads where SoapUI is having some challenges.
With these scripts you can upload a large file to the file-server.
Get the GB external reference (sender url)
And download that file using the sender url and a client certificate.

## pre-requisites

1) a user (with id 0) has to be available.. The id can be modified in `fs-upload.sh` script and the certificate passed in, has to match the public part of the certificate in the keystore.p12.
2) a client certificate in a file called keystore.p12 (secured with password *password*) for use in the `fs-gb-download.sh` and `fs-create-user.sh` scripts
3) cli tools like keytool, base64, curl, grep and sed and a bash shell

## how to use

When using the default url on localhost, the url parameter for the scripts can be omitted.
0) assuming an empty environment create a user using `./fs-create-user.sh localhost http://localhost:8080`, this assumes that a keystore.p12 is available with a password *password* having a certificate with alias *localhost*. The script outputs the userid created. 0 is expected since it should be the first user. If not either clean the environment or modify the `upload-fs.sh` script accordingly.
1) find or create a large (random) file for transfer, for instance using `dd if=/dev/urandom of=bigfile.bin bs=8192 count=16384`
2) upload the file using `./fs-upload.sh bigfile.bin http://localhost:8080`, when succesfully uploaded a string is returned.
3) pass the string from 2 into the following command to retrieve a download url `./fs-gb-reference.sh <token> http://localhost:8080`. Executing this command returns an url. This url is regularly passed to a party who then can retrieve that file.
4) retrieve the file using the client certificate in keystore.p12 and the url from 3. `./fs-gb-download.sh <url> <optional: filename to save to>`. If no filename is passed the file will be saved to the current directory with a timestamp and a prefix *download*.
