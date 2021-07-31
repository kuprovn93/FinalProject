import React,{  Component } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { Button, TextField } from '@material-ui/core';
import { auth } from "../services/firebase";
import { db } from "../services/firebase";
import firebase from 'firebase';
import BoardListItem from "../components/BoardComponent"


export default class BoardChat extends Component {
    constructor(props) {
        super(props);
        this.state = {
          user: auth().currentUser,
          board: '',
          boards:[],
          message:'',
          readError: null,
          writeError: null,
          loadingPosts: false,
          userRole:null,
          boardUid:""
        
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);   
        this.myRef = React.createRef();
      }

      async componentDidMount() {
        auth().onAuthStateChanged(function(u) {
          console.log(u);
                console.log(u );
            });
        this.setState({ readError: null, loadingChats: true });
        const chatArea = this.myRef.current;
        
        var docRef = db.collection("Users").doc(this.state.user.uid);
    
        docRef.get().then((doc) => {
        if (doc.exists) {
            this.setState({userRole: doc.data().UserRole});
        } else {
            // doc.data() will be undefined in this case
            console.log("No such document!");
        }
        }).catch((error) => {
        console.log("Error getting document:", error);
        });
    
        try {
          db.collection("Boards").onSnapshot( snapshot => {
    
            let boards = [];
            snapshot.forEach((snap) => {
              boards.push(snap.data());
            });
            console.log(boards);
            // posts.sort(function (a, b) { return b.CreatedAt - a.CreatedAt })
            this.setState({ boards });
            // chatArea.scrollBy(0, chatArea.scrollHeight);
            // this.setState({ loadingChats: false });
          });
        } catch (error) {
          this.setState({ readError: error.message, loadingChats: false });
        }
      }
      
      async handleSubmit(event) {
        event.preventDefault();
        console.log(this.state.message);
        this.setState({ writeError: null });
        
        // const chatArea = this.myRef.current;
        try {
          if (this.state.message === ""){
            throw ("Message Cannot be empty")
          }
          db.collection("Boards").add({
            PostData: this.state.post,
            CreatedAt: firebase.firestore.FieldValue.serverTimestamp(),
            Author: this.state.user.email
          });
          
          console.log("post save successfully");
          // chatArea.scrollBy(0, chatArea.scrollHeight);
        } catch (error) {
          console.log(error.message);
          this.setState({ writeError: error.message });
        }
        this.setState({message: '' });
      }
    
      handleChange(event) {
        this.setState({
          board: event.target.value
        });
        
      }
    
    
      async formatTime(timestamp) {
        const d = new Date(timestamp);
        const time = `${d.getDate()}/${(d.getMonth()+1)}/${d.getFullYear()} ${d.getHours()}:${d.getMinutes()}`;
        return time;
      }


  
        render() {
            return (
              
              <div className = "App">
                
                <Header />
                <div>
                 {/* {    
                this.state.boards.map(board => {
                    return (
                        <BoardListItem board={board.BoardName} boardImg={board.BoardImage} />
                    )
                })
                } */}
                </div>
        {/* 
                <h1>Your FanApp Page </h1>
                <div>
                <div className="chat-area form-element" ref={this.myRef}>
           
                  {this.state.loadingChats ? <div className="spinner-border text-success" role="status">
                    <span className="sr-only">Loading...</span>
                  </div> : ""}
                  
                  {this.state.posts.map(chat => {
                    return <PostListItem post={chat.PostData} author={chat.Author} timestamp={chat.CreatedAt.seconds}>
                    <br/>
                    </PostListItem>
                  })}
                </div>
                </div>
                <br></br> */}
        
                
                {/* if (db.collection("Users").doc(auth().currentUser.uid.UserRole.localeCompare("Admin") === 0)) {
                  classes.push("admin-user")
                }
                else {
                classes.push("customer-user")
                } */}
        
               
                
                <form onSubmit={this.handleSubmit} className={this.state.userRole === "Customer" ? "admin-user": "admin-user"}>
                  <TextField  className="form-element" label="How are you feeling today? " name="post" onChange={this.handleChange} value={this.state.message}></ TextField >
                  {this.state.error ? <p className="text-danger">{this.state.error}</p> : null}
                  <Button variant="contained" type="submit" className="btn btn-submit px-5 mt-4">Send </Button>
                </form>
        
               
        
        
              <Footer></Footer>
              </div>
              
             
            );
          }
        }