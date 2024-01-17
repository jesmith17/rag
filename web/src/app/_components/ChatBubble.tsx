 import React from "react"
import Image from "next/image"

interface ChatBubbleProps {
    content: String
    imgSrc: string 
    reverse: Boolean
}

const defaultProps:ChatBubbleProps = {
    content: 'Tell me about MongoDB.',
    imgSrc: '/chat.svg',
    reverse: false
}

 export default function ChatBubble(Props: ChatBubbleProps) {
    const { 
        content = defaultProps.content,
        imgSrc = defaultProps.imgSrc,
        reverse = false
    }  = Props

    const direction =  reverse ? 'flex-row-reverse' : 'flex-row'

    return (
        <li className={`flex ${direction} items-start gap-2.5 text-spring-green`}>
            <div className="relative h-8 w-8">
                <Image
                className="rounded-full"
                src={imgSrc} 
                alt={'Enter'}
                fill={true}
                />

            </div>
            <div className="flex flex-col self-end mt-2 w-full md:max-w-[640px] max-w-[320px] leading-1.5 px-4 py-2 border-gray-200 bg-evergreen rounded-e-xl rounded-es-xl dark:bg-gray-700">

            <p className="text-sm font-normal py-2.5 dark:text-white">{content}</p>
            {/* {reverse &&
                <div>
                    Feedback Panel
                </div>
            } */}

            </div>

        </li>
    )
 }


