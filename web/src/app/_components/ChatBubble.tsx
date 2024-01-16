 import React from "react"
import Image from "next/image"

interface ChatBubbleProps {
    content: String
    imgSrc: string 
}

const defaultProps:ChatBubbleProps = {
    content: 'Tell me about MongoDB.',
    imgSrc: '/chat.svg'
}

 export default function ChatBubble(Props: ChatBubbleProps) {
    const { 
        content = defaultProps.content,
        imgSrc = defaultProps.imgSrc
    }  = Props

    return (
        <li className="flex items-start gap-2.5">
            <div className="relative h-8 w-8">
                <Image
                className="rounded-full"
                src={imgSrc} 
                alt={'Enter'}
                fill={true}
                />

            </div>
            <div className="flex flex-col self-end mt-2 w-full max-w-[320px] leading-1.5 px-4 py-2 border-gray-200 bg-gray-100 rounded-e-xl rounded-es-xl dark:bg-gray-700">

            <p className="text-sm font-normal py-2.5 text-gray-900 dark:text-white">{content}</p>
            </div>

        </li>
    )
 }


