import React, { Component } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { Link } from 'react-router-dom';
import "../App.css";


export default class HomePage extends Component {
  render() {
    return (
        
          // <div style={{ backgroundImage: `url(${background})` }}>
          <div id="login-page">
          <div id="login-card">
          <Header className="App-header"></Header>
            <div  style = {{}}>
              <h1 class = "form-element">Welcome to SocialBoard &#128512;</h1>
              <h2 class="form-element" variant="contained" color="primary">ChatBoards For The New Age !</h2>
              <div class="form-element">
                <Link className="btn btn-primary px-5 mr-3" to="/signup">Create New Account</Link>
                
              </div>
              <div class="form-element">
                
                <Link className="btn px-5" to="/login">Login to Your Account</Link>
              </div>
            </div>
            <Footer></Footer>
          </div>
          </div>
        
        
    
    )
  }
}