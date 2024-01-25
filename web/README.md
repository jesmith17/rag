
  

# Web Front end

  

![](https://webimages.mongodb.com/_com_assets/cms/kuyjf3vea2hg34taa-horizontal_default_slate_blue.svg?auto=format%252Ccompress)


## Requirements
  - **Minimum Node Version 16.14.0**. Can use something like [NVM](https://github.com/nvm-sh/nvm) if concerned about upgrading.

### Run the App
```shell
npm install
npm run dev
```

### Configuring the App
If you want to be able to change which backend service the RAG optimizer web app uses follow the following instructions

1.  `cp .env.template .env.local` Copy the template environment file
2. Edit the `NEXT_PUBLIC_API_BASE_URL` environment variable to point toward the backend. 
3.  Reload the App

**Note:** Include both the port and protocol. For example `http://localhost:8080`
  

### Loading the App

Navigate to http://localhost:3000/