 import React, { useState } from "react"
import Image from "next/image"
import useFeedback from "../_utilities/useFeedback"
import { toast } from "react-toastify"

interface ChatBubbleProps {
    feedbackId: String
    content: String
    imgSrc: string 
    reverse: Boolean
}

const defaultProps:ChatBubbleProps = {
    feedbackId: '',
    content: 'Tell me about MongoDB.',
    imgSrc: '/chat.svg',
    reverse: false
}

 export default function ChatBubble(Props: ChatBubbleProps) {
    const { 
        content = defaultProps.content,
        feedbackId = defaultProps.feedbackId,
        imgSrc = defaultProps.imgSrc,
        reverse = false
    }  = Props

    const direction =  reverse ? 'flex-row-reverse' : 'flex-row'

    const [feedbackNotProvided, setFeedbackNotProvided] = useState(true)

    const handlePositiveFeedback = (e:any) => {
        const response = useFeedback(
            feedbackId,
            { 
            thumbsUp: true,
            justification: {
                complete: true
            }
        })
        toast.promise(response,{
            pending: 'Submitting Feedback',
            success: 'Thank you',
            error: 'Error'
        })
        setFeedbackNotProvided(false)
    }

    const handleNegativeFeedback = (e:any) => {
        const response = useFeedback(
            feedbackId,
            {  
            thumbsUp: false,
            justification: {
                complete: true
            }
        })
        toast.promise(response,{
            pending: 'Submitting Feedback',
            success: 'Thank you',
            error: 'Error'
        })
        setFeedbackNotProvided(false)
    }

    return (
        <li className={`flex ${direction} items-start gap-2.5 ${reverse? 'text-gray-100': 'text-gray-100'}`}>
            <div className="relative h-8 w-8">
                <Image
                className="rounded-full"
                src={ reverse? './logomark.svg' : imgSrc} 
                alt={'Enter'}
                fill={true}
                />

            </div>
            <div className={`${reverse? 'bg-evergreen': 'bg-forest-green'} flex flex-col self-end mt-2 w-full md:max-w-[640px] max-w-[320px] leading-1.5 px-4 py-2 border-gray-200 rounded-e-xl rounded-es-xl dark:bg-gray-700`}>

            <p className="text-sm font-normal py-2.5 dark:text-white">{content}</p>
            {reverse && feedbackNotProvided &&
                <div className="w-full flex flex-row gap-x-4 ">
                    <button onClick={handlePositiveFeedback} className="relative h-8 w-8 hover:bg-forest-green rounded-lg" value="positive" type="button">
                        <Image
                            className="rounded-full"
                            src="/thumbs-up.svg" 
                            alt={'Enter'}
                            fill={true}

                        />
                    </button>
                    <button onClick={handleNegativeFeedback} className="relative h-8 w-8 hover:bg-forest-green rounded-lg rotate-180" value="negative" type="button">
                        <Image
                            className="rounded-full"
                            src="/thumbs-up.svg" 
                            alt={'Enter'}
                            fill={true}

                        />
                    </button>
                </div>
                
            }

            </div>

        </li>
    )
 }


