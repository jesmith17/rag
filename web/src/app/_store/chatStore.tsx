import {create} from 'zustand'
import axios from "axios";
import { toast } from 'react-toastify';


const url = `${process.env.NEXT_PUBLIC_API_BASE_URL}/text`

type ChatState = {
    messages: ChatMessage[]
    responseMessage: String
    processing: boolean,
    settings: RAGSettings,
    addMessage: (message:ChatMessage) => void
    sendMessage: (message:chatApiParams) => void
    acknowledgeMessage: () => void
    changeSetting: (settings:RAGSettings) => void
    clearMessages: () => void
}

type ChatMessage = {
    feedbackId: String
    content: String
    reverse: boolean
}

type RAGSettings = {
    chunkSize: number
    numCandidates: number
    limit: number
    similarity: "euclidean" | "cosine" | "dotproduct"
}

type chatApiParams = {
    prompt : String
    settings: RAGSettings
}

type chatResponse = {
    status: Number
    prompt: String
    answer: String
    llmInput: String
    feedbackId: String
}

const defaults = {
    chunkSize: 1000,
    numCandidates: 100,
    limit: 10,
    similarity: 'euclidean'
}

const message = async (reqBodyParams:chatApiParams): Promise<chatResponse> => {


    console.log(reqBodyParams)
    const result = await axios.post(
        url,
       {
            prompt: reqBodyParams.prompt,
            chunkSize: reqBodyParams.settings.chunkSize,
            numCandidates: reqBodyParams.settings.numCandidates,
            limit: reqBodyParams.settings.limit,
            similarity: reqBodyParams.settings.similarity

        }
    )

    return {
        status: result.status,
        prompt: result.data.prompt,
        answer: result.data.answer,
        llmInput: result.data.llmInput,
        feedbackId: result.data.feedbackId
    }
}

export const useChatStore = create<ChatState>((set) => ({
    messages: [],
    processing: false,
    responseMessage: '',
    settings: {
        chunkSize: 1000,
        numCandidates: 100,
        limit: 10,
        similarity: 'euclidean'
    },
    addMessage: (message:ChatMessage) => {
        set((state) => ({
            messages: [
                ...state.messages,
                message
            ],
            processing: true
          }))
    },
    sendMessage: (params:chatApiParams) => {
       
        const promise = message(params).then(response => {
            if (response.status!==200){
                return
            }
            set((state) => ({
                processing: false,
                messages: [
                    ...state.messages,
                    {
                        feedbackId: response.feedbackId,
                        content: response.answer,
                        reverse: true
                    }
                ]
            }))
        })
        toast.promise(promise,{
            pending: 'Processing...',
        })
    },
    acknowledgeMessage: () => {
        set((state) => ({
            processing: false
        }))
    },
    changeSetting: (settings:RAGSettings) => {
        set((state) => ({
            settings: settings
        }))
    },
    clearMessages: () => {
        set((state) => ({
            messages: []
        }))
    }
}))

export {}