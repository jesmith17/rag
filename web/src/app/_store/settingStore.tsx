import {create} from 'zustand'

type SettingState = {
    isShown: boolean,
    toggleState: () => void;
}

export const useSettingStore = create<SettingState>((set) => ({
    isShown: false,
    toggleState: () => {
        set((state) => ({
            isShown: !state.isShown
        }))
    }
}))

export {}