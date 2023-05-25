import React, { useEffect, useState } from "react"
import styles from "./Lobbies.module.css"
import axios from "axios"

export const Lobbies = () => {
    const [lobbies, setLobbies] = useState()
    useEffect(() => {
        axios.get("http://localhost:4000/signin")
        .then((response) => {
            console.log(response)
        })
    })
    return <div className={styles.lobbiesWrapper}>

    </div>
}