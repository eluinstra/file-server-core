# File Server Core

Implementents the core for an HTTP file up and download server. It provides file download over HTTPS using HTTP GET (Ranges are supported) and file upload using the tus protocol. It can be used for Grote Berichten file transfer.

### TODO

##### Download

* add option to encrypt files with user's certificate (and implement .certificate extension)
  * register separate encryption certificate(s)

##### Upload

* implement tus concatenation

