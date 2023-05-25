import React, { useEffect, useState } from 'react';
import './App.css';
import { Lobbies } from './pages/lobbies/Lobbies';
import axios from 'axios';
import { Auth } from './pages/auth/Auth';
import { User } from './type/type';


function App() {
  const [user, setUser] = useState<User | null>(null)
  return (
    <div className="App">
      {!user ? <Auth setUser={setUser} />: <Lobbies />}
    </div>
  );
}

export default App;
