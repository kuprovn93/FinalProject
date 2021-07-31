import React from 'react';
import { auth } from '../services/firebase';
import { Button } from '@material-ui/core';
import { Link } from 'react-router-dom';
import { logout } from '../helpers/auth.js'
import "../App.css";
import "../styles.css";
function Header() {
  return (
    <header>
      <nav className="nav">
        <div className="nav-item" >
        <Link className=" nav-link" to="/">Social-App</Link>
        </div>
        
          {auth().currentUser
            ? <div className="nav-item">
              <Link className="nav-item nav-link" to="/boards">Profile</Link>
              <Button className="nav-item nav-link" variant="contained" onClick={() => logout()}>Logout</Button>
            </div>
            : <div className="nav-item">
              <Link className="nav-item nav-link" to="/login">Log In</Link>
              <Link className="nav-item nav-link" to="/signup">Create New Account</Link>
            </div>}
        
      </nav>
    </header>
  );
}

export default Header;