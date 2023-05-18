import React, { useEffect, useState } from 'react';
import './App.css';
import { Lobby } from './pages/lobby/Lobby';
import axios from 'axios';

function App() {
  useEffect(() => {
    axios.post('http://localhost:4000/signin', {
      "name": "admin",
      "password": "111"
  })
    .then((response) => {
      console.log(response);
    })
    .catch((error) => {
      console.log(error.response);
    });
    // axios.get(`http://localhost:4000/hello/world`)
    // .then((response) => {
    //   console.log(response);
    // })
    // .catch((error) => {
    //   console.log(error.response);
    // });
  },[])
  return (
    <div className="App">
      <Lobby />
    </div>
  );
}

export default App;
