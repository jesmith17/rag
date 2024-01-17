"use client";

import Image from 'next/image'
import ChatBubble from './_components/ChatBubble'
import { useChatStore } from './_store/chatStore'
import { useSettingStore} from './_store/settingStore'
import { ToastContainer, toast } from 'react-toastify';
import {  useState } from 'react'
import 'react-toastify/dist/ReactToastify.css';

export default function Home() {

  const { messages, processing, addMessage, sendMessage, settings, changeSetting } = useChatStore()

  const [draftMessage, setDraftMessage] = useState("")

  const {isShown, toggleState}  = useSettingStore()

  const handleChat = (e:any) => {
    e.preventDefault()
    if(draftMessage === "") return
    addMessage({
      feedbackId: '',
      content: draftMessage,
      reverse: false
    })

    sendMessage({
      prompt: draftMessage,
      settings: settings
    })

    setDraftMessage("")
    
  }

  const handleChunkChange = (e:any) => {
    changeSetting({
      ...settings,
      chunkSize: e.target.value
    })
  }

  const handleNumChange = (e:any) => {
    changeSetting({
      ...settings,
      numCandidates: e.target.value
    })
  }

  const handleLimitChange = (e:any) => {
    changeSetting({
      ...settings,
      limit: e.target.value
    })
  }

  const handleSimilarityChange = (e:any) => {

    changeSetting({
      ...settings,
      similarity: e.target.value
    })
  }

  const handleSettingChange = (e:any) => {
    toggleState()
  }

  return (
    <main className="min-h-dvh  p-2 lg:p-4 flex flex-col text-slate-blue bg-slate-blue">
      <div className="w-full flex flex-row start pb-8 ">
        <img 
          className="w-64"
          src="/mongo-spring-green.png" 
        />
        <div className="lg:px-2 self-end text-spring-green">RAG Optimizer</div>
      </div>
      <div className="min-h-full flex-1 self-start flex flex-col justify-between w-full  p-2 items-center  p-2 rounded-md   rounded-md ">
            <div id="chat" className="w-full pb-4">
              <ul className="space-y-4" >
                  {messages.length > 0 &&
                      messages.map((message, index )=> {
                        return (
                          <ChatBubble
                            imgSrc='/chat.svg'
                            feedbackId={message.feedbackId}
                            content={message.content}
                            reverse={message.reverse}
                            key={`message-${index}`}
                          />
                        )
                    })

                    }
              </ul>
              <div id="chat-anchor"></div>
            </div>



        </div>
        {isShown &&
          <div className="text-spring-green w-full bg-evergreen flex flex-row justify-between p-4">
            <div className="flex flex-col h-11 w-full px-3">
              <label className="">Chunk Size:</label>
              <input onChange={handleChunkChange} type="text" className="border bg-transparent  outline outline-0 border-0 border-b border-spring-green" placeholder={`${settings.chunkSize}`} />
            </div>
            <div className="flex flex-col h-11 w-full px-3">
              <label>NumOfCandidates:</label>
              <input onChange={handleNumChange} type="text"  className="border bg-transparent  outline outline-0 border-0 border-b border-spring-green" placeholder={`${settings.numCandidates}`} />
            </div>
            <div className="flex flex-col h-11 w-full px-3">
              <label>Limit:</label>
              <input onChange={handleLimitChange} type="text" className="border bg-transparent  outline outline-0 border-0 border-b border-spring-green" placeholder={`${settings.limit}`} />
            </div>
            <div className="flex flex-col h-11 w-full px-3">
              <label>Similarity:</label>
              <select onChange={handleSimilarityChange} className="bg-transparent" name="similarity" id="sim">
                <option value="euclidean" selected={settings.similarity === 'euclidean'? true: false}>Euclidean</option>
                <option value="cosine" selected={settings.similarity === 'cosine'? true: false}>Cosine</option>
                <option value="dotproduct" selected={settings.similarity === 'dotproduct'? true: false}>Dotproduct</option>
              </select>
            </div>
          </div>
        }
        {messages.length == 0 &&
          <div className="text-spring-green self-end flex flex-col gap-y-16 p-16 text-center w-full text-xl">
                <h2 className="text-4xl font-bold">Welcome to the Rag Optimizer</h2>
                <div className="flex flex-col gap-y-4">
                <span>Anyone can build RAG Applications! </span>
                
                <span>The bigger challenge is to generate the best vector embeddings to achieve accuracy of the RAG architecture. </span>
                <span>RAG Optimizer is a framework that allows developers to optimize the chunking strategy.</span> 
                </div>

            </div>
        }
        <form className="flex flex-row w-full p-2 self-end" onSubmit={handleChat}>
            <div className="rounded-full border border-spring-green p-1  px-4 text-left placeholder-spring-green text-slate-blue w-full">

                <input 
                  type="text"
                  disabled={processing}
                  value={draftMessage}
                  className="w-full focus:outline-none placeholder-forest-green text-spring-green focus:placeholder-forest-green bg-transparent"
                  placeholder="Enter a Prompt Here"
                  onChange={e => setDraftMessage(e.target.value)}
                />
            </div>
            <button type="button" onClick={handleSettingChange} className="relative h-8 w-8 hover:bg-evergreen rounded-lg">

            <Image
                className="rounded-full"
                src="/settings.svg" 
                alt={'Settings'}
                fill={true}

              />
            </button>

            <button className="relative h-8 w-8 hover:bg-evergreen rounded-lg" onClick={handleChat} type="submit">
              <Image
                className="rounded-full"
                src="/send.svg" 
                alt={'Enter'}
                fill={true}

              />
            </button>
        </form>
    <ToastContainer 
      position="bottom-center"
      toastStyle={{
        backgroundColor: "#00684A",
        color: "#00ED64"
      }}
      autoClose={5000}
      hideProgressBar={true}
      newestOnTop={false}
    />
    </main>
  )
}

//"top-0 left-0 w-64 h-screen transition-transform -translate-x-full sm:translate-x-0"

//flex flex-row justify-between bg-white

{/* <aside id="sidebar" className="flex flex-col w-2/5 overflow-y-auto h-full justify-top text-spring-green" aria-label='sidebar'>
<div className="h-full px-3 py-4 overflow-y-auto ">
    <ul className="space-y-2 font-medium">
      <li className="hover:bg-forest-green cursor-pointer p-2">Chat 1</li>
      <li className="hover:bg-forest-green cursor-pointer p-2">Chat 2</li>
    </ul>
</div>
</aside> */}

{/* <div className="w-full lg:px-2 flex flex-col justify-between  items-center max-h-dvh ">
<div className="w-full  flex flex-col overflow-auto">
</div>
      </div> */}
