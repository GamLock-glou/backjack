import { Button, TextField } from "@mui/material"
import React, { useEffect, useState } from "react"
import styles from "./Auth.module.css"
import { User } from "../../type/type"
import { useAuthUserMutation } from "../../services/UserService"
import { isFetchBaseQueryError } from "../../services/helpers"
import { useAppDispatch } from "../../hooks/redux"
import { setUser } from "../../store/reducers/UserSlice"
import { Loading } from "../loading/Loading"
import { useNavigate } from "react-router-dom"


export const Auth = () => {
    const dispatch = useAppDispatch();
    const navigate = useNavigate();
    const [name, setName] = useState<string>('')
    const [errorAuthMessage, setErrorAuthMessage] = useState<string>('')
    const [password, setPassword] = useState<string>('')
    const [
        signin,
        {
            error,
            isError,
            isSuccess,
            isLoading,
            data
        }
    ] = useAuthUserMutation();
    useEffect(() => {
        if(isError) {
            if(isFetchBaseQueryError(error)) {
                setErrorAuthMessage(error.data.message)
            }
        }
        if(isSuccess && data) {
            dispatch(setUser(data))
        }
    }, [data, error, isSuccess, isError])
    const handleSignIn = async () => {
        await signin({name, password})
    }
    return <div className={styles.authWrapper}>
        <h1>
            Wellcom to auth in BlackJack
        </h1>
        <div className={styles.authTextFieldsWrapper}>
            <TextField
                value={name}
                onChange={(e) => setName(e.target.value)}
                label="Name"
                variant="standard"
            />
            <TextField
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                type="password"
                label="Password"
                variant="standard"
            />
        </div>
        {
            isLoading ?
            <Loading /> :
            <div className={styles.authButtonWrapper}>
                <Button onClick={handleSignIn} variant="outlined">Login</Button>
            </div>
        }
        {
            errorAuthMessage &&
            <div>
                {errorAuthMessage}
            </div>
        }
        <div className={styles.infoRegBlock}>
            Are you not registered yet? Click here
        </div>
    </div>
}