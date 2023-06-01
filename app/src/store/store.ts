import { combineReducers, configureStore } from "@reduxjs/toolkit";
import { userService } from "../services/UserService";
import UserReducer from "./reducers/UserSlice";


const rootReducer = combineReducers({
    UserReducer,
    [userService.reducerPath]: userService.reducer
})

export const setupStore = () => {
    return configureStore({
        reducer: rootReducer,
        middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(userService.middleware)
    })
}

export type RootState = ReturnType<typeof rootReducer>
export type AppStore = ReturnType<typeof setupStore>
export type AppDispatch = AppStore['dispatch']