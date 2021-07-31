
import React, { Component } from "react";
import { Link } from "react-router-dom";
import { signin, signInWithGoogle } from "../helpers/auth";
import { TextField, Button } from '@material-ui/core';
import Footer from '../components/Footer';
import "../index.css";

export default class Login extends Component {
  constructor() {
    super();
    this.state = {
      error: null,
      email: "",
      password: ""
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
    this.setState({ error: "" });
    try {
      await signin(this.state.email, this.state.password);
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
        <div id="login-card">
          
        <form
          className="form"
          autoComplete="off"
          onSubmit={this.handleSubmit}>
          <h1 >
            Login to 
            <Link className="nav-link" to="/">
               SocialBoard
            </Link>
          </h1>
          <p className="lead">
            Fill in the form below to login to your SocialBoard account.
          </p>
          <div className="form-element">
            <TextField
              className="form-control"
              placeholder="Email"
              name="email"
              type="email"
              onChange={this.handleChange}
              value={this.state.email}
            />
          </div>

          <div className="form-element">
            <TextField
              className="form-control"
              placeholder="Password"
              name="password"
              onChange={this.handleChange}
              value={this.state.password}
              type="password"/>
          </div>

          <div className="form-element">
            {this.state.error ? (
              <p className="text-danger">{this.state.error}</p>
            ) : null}
            <Button variant="contained" color="primary" type="submit">Login</Button>
          </div>
          
          <p>You can also log in with these social media apps</p>
          <Button variant="contained" color="secondary"
          onClick={this.googleSignIn}>
            Sign in with Google
          </Button>
      
          <hr />
          <p className="form-element">
            Don't have an account? <Link to="/signup">Sign up</Link>
          </p>
        </form>
        <hr />
        <Footer></Footer>
        </div>
      </div>
    );
  }
}
