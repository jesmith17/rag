import Image from 'next/image'
import ChatBubble from './_components/ChatBubble'


export default function Home() {
  return (
    <main className="h-screen  items-center justify-between p-4 flex flex-col text-slate-blue bg-evergreen">
      <div className="w-full flex flex-row items-center pb-8 ">
        <img 
          className="w-64"
          src="/mongo-spring-green.png" 
        />
        <div className="px-2 self-end text-spring-green">North Hackathon</div>
      </div>
      <div className="flex flex-row  justify-between w-full h-full">
      <aside id="sidebar" className="flex flex-col w-2/5 overflow-y-auto h-full justify-top text-spring-green" aria-label='sidebar'>
        <div className="h-full px-3 py-4 overflow-y-auto ">
            <ul className="space-y-2 font-medium">
              <li className="hover:bg-forest-green cursor-pointer p-2">Chat 1</li>
              <li className="hover:bg-forest-green cursor-pointer p-2">Chat 2</li>
            </ul>
        </div>
      </aside>
      <div className="w-full px-2 flex flex-col justify-between h-full">
        <div className="p-2 rounded-lg bg-white shadow-xl h-full flex flex-col shadow-xl border border-slate-blue ">
          <div className="h-full">
            <ul className="space-y-4">
                <ChatBubble />
                <ChatBubble />
            </ul>
          </div>
          <div className="flex flex-row">
          <div className="rounded-full border border-gray-500 p-1  px-4 text-left placeholder-gray-600 text-slate-blue w-full">
              <input 
                type="text"
                className="w-full focus:outline-none focus:placeholder-gray-400"
                placeholder="Enter a Prompt Here"
              />
          </div>
          <button className="relative h-8 w-8 hover:bg-gray-100 rounded-lg">
            <Image
              className="rounded-full"
              src="/send.svg" 
              alt={'Enter'}
              fill={true}

            />
          </button>

          </div>

        </div>

      </div>
      </div>

    </main>
  )
}

//"top-0 left-0 w-64 h-screen transition-transform -translate-x-full sm:translate-x-0"

//flex flex-row justify-between bg-white