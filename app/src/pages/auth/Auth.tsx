import { Button, TextField } from "@mui/material"
import React, { useState } from "react"
import styles from "./Auth.module.css"
import axios from "axios"
import { User } from "../../type/type"
import { queries } from "../../api/api"


interface AuthProps {
    setUser: (v: User) => void
}

export const Auth = ({setUser}: AuthProps) => {
    const [name, setName] = useState<string>("")
    const [password, setPassword] = useState<string>("")
    const onClick = () => {
        // axios.post("http://localhost:4000/signin", {name, password})
        // .then((response)=>{
        //     setUser(response.data)
        // })
        queries.signin(name, password)
    }
    return <div className={styles.authWrapper}>
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
        <div className={styles.authButtonWrapper}>
            <Button onClick={onClick} variant="outlined">Login</Button>
        </div>
    </div>
}