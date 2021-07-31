import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { signup, signInWithGoogle } from "../helpers/auth";

import { TextField, Button } from '@material-ui/core';
import Footer from '../components/Footer';


export default class SignUp extends Component {
  constructor() {
    super();
    this.state = {
      error: null,
      fname: '',
      lname: '',
      email: '',
      password: '',
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.googleSignIn = this.googleSignIn.bind(this);
    
  }

  handleChange(event) {
    this.setState({
      [event.target.name]: event.target.value
    });
  }

  async handleSubmit(event) {
    event.preventDefault();
    this.setState({ error: '' });
    try {

    console.log("firstname signup", this.state.fname);
      await signup(this.state.email, this.state.password, this.state.fname, this.state.lname);
      
    } catch (error) {
      this.setState({ error: error.message });
    }
  }

  async googleSignIn() {
    try {
      await signInWithGoogle();
    } catch (error) {
      this.setState({ error: error.message });
    }
  }



  render() {
    return (
      <div id="login-page">
        <div id="login-card" class="signup-card">
        <form 
        className="form" 
        onSubmit={this.handleSubmit}
        >
          <h1>
            Sign Up to
          <Link className="nav-link" to="/">
              SocialBoard</Link>
          </h1>
          <p className="lead">
              Fill in the form below to create an SocialBoard account.</p>
          
          <div className="form-element">
            <TextField className="form-control" 
            name="fname" 
            placeholder="First Name"  
            onChange={this.handleChange} 
            value={this.state.fname} 
            type="text"></TextField>
          </div>
          <div className="form-element">
            <TextField className="form-control"
            placeholder="Last Name"  
            name="lname" 
            onChange={this.handleChange} 
            value={this.state.lname} 
            type="text"></TextField>
          </div>

          <div className="form-element">
            <TextField
            className="form-control" 
            placeholder="Email" 
            name="email" 
            
            onChange={this.handleChange} 
            value={this.state.email}>
            </TextField>
          </div>

          <div className="form-element">
            <TextField className="form-control" 
            placeholder="Password" 
            name="password" 
            onChange={this.handleChange} 
            value={this.state.password} 
            type="password"></TextField>
          </div>

          <div className="form-element">
            {this.state.error ? <p className="text-danger">{this.state.error}</p> : null}
            <Button variant="contained" color="primary" type="submit">Sign up</Button>
          </div>
          <p>You can also sign up with these social media apps</p>
          <Button variant="contained" color="secondary"
          onClick={this.googleSignIn}>
            Sign up with Google
          </Button>
         
          <hr></hr>
          <p>Already have an account? <Link to="/login">Login</Link></p>
        </form>
        <hr></hr>
        <Footer></Footer>
      </div>
      </div>
    )
  }
}