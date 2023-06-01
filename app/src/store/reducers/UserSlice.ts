import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { IUser } from "../../models/IUser";
interface UserState {
  user: IUser | null
  isAuth: boolean
}
const initialState: UserState = {
  user: null,
  isAuth: false
}

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setUser(state, action: PayloadAction<IUser>) {
      localStorage.setItem('token', action.payload.token)
      state.user = action.payload
      state.isAuth = true
    },
    signOut(state) {
      state.user = null
      localStorage.removeItem('token')
    }
  }
})

export default userSlice.reducer;

export const {setUser, signOut} = userSlice.actions