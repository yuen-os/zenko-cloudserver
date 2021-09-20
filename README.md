# Zenko Cloudserver ReadMe

Zenko cloudserver is a private cloudserver that uses aws s3 sdk as it's API, the sdk we used here is aws s3 java sdk version 2 (2.17.35), this is only a cloudserver not the whole infrastructure, the zenko infrastructure also consist of a UI (Orbit) and uses Kubernetes to provision the infrastructure

- [cloudserver docs](https://s3-server.readthedocs.io/en/latest/)
- [cloudserver docker image](https://hub.docker.com/r/zenko/cloudserver)
- [zenko infrastructure](https://zenko.readthedocs.io/en/latest/installation/index.html)
- [cyberduck client](https://cyberduck.io/download/)
- [cyberduck s3 profile](https://cyberduck.io/s3/)
- [aws s3 sdk quickstart](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-s3.html)
- Mounting docker volumes

    ```

    pull docker image:
    https://hub.docker.com/r/zenko/cloudserver

    create volume:
    docker volume create cloudserver

    after creating volume go to the volume path (windows):
    \\wsl$\docker-desktop-data\version-pack-data\community\docker\volumes\

    after going to path go the your docker volume:
    cloudserver/_data

    and create 2 folders :
    localData
    localMetaData

    after creation of 2 folders inside the volume run this docker command mounting those folders with the dir inside of container:
    docker run -d -p 8000:8000 --name cloudserver -v /var/lib/docker/volumes/cloudserver/_data/localData:/usr/src/app/localData -v /var/lib/docker/volumes/cloudserver/_data/localMetadata:/usr/src/app/localMetadata -e REMOTE_MANAGEMENT_DISABLE=1 zenko/cloudserver
    ```

- Features

    ```
    ACL
    - only supports accessKey and secretKey but for finer grain authorization doesn't support since it needs aws iam

    Versioning
    - supported
     
    LifeCycle
    - supported on zenko infrastructure but on standalone cloudserver no

    Replication
    - supported on zenko infrastructure but on standalone cloudserver no, and only supports NFS and cloudprovider, cannot replicate on localstorage

    Encryption
    - supports default SSE-AES256 

    Pre-sign Url
    - supported

    things to check for production grade:

    High Availability using NFS (different servers and clients)
    - can't test this one since it requires different vms
    - spawning multiple containers on same vms doesn't solve the availability it's just horizontal scaling 

     
    Duplicity
    - backup your files on cloudstorage
    - https://s3-server.readthedocs.io/en/latest/INTEGRATIONS.html#duplicity

     

    Virus detection?
    - ClamAV
    - this not part of cloudserver nor afaik s3 doesn't have default virus detection

    Deployment
    - how do we deploy this using docker?
     

    Integration
    - do we need to integrate with aws,azure,google cloud or our service will just purely rely on local storage (vms or docker volumes)
    ```

- Running the API
    - start the cloudserver
    - start the application (change the credentials on [application.properties](http://application.properties) if needed)
    - go to [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) for documentation (change port if needed)