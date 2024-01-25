import axios from "axios"

const url = `${process.env.NEXT_PUBLIC_API_BASE_URL}/feedback`

type feedbackParams = {
    thumbsUp: boolean
    justification: {
        complete: boolean
    }
}

type feedbackResponse = {
    message: String
}
const submitFeedback = async (id:String, feedbackParams:feedbackParams): Promise<feedbackResponse> => {

    const result = await axios.post(
        `${url}/${id}`,
       feedbackParams
    )

    console.log(result)

    return {
        message: 'Thank you for your feedback'
    }
}

export default submitFeedback