import { auth } from "../services/firebase";

import firebase from 'firebase';

export async function signup(email, password, fname, lname) {
    
  try {
        
        const userCredential = await auth().createUserWithEmailAndPassword(email, password);
        const usersRef = firebase.firestore().collection('Users');
        usersRef.doc(`${userCredential.user.uid}`).set({
            FirstName: fname,
            LastName: lname,
            Email: email,
            UserRole: "Customer",
            RegisteredOn: new Date().toLocaleString() + ''
        });
    } catch (error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;
        // [START_EXCLUDE]
        if (errorCode === 'auth/weak-password') {
            alert('The password is too weak.');
        } else {
            alert(errorMessage);
        }
        console.log(error);
    }
}

export function signin(email, password) {
  return auth().signInWithEmailAndPassword(email, password);
}

export async function signInWithGoogle() {
  const provider = new auth.GoogleAuthProvider();
  try {
        const result = await auth().signInWithPopup(provider);
        var credential = result.credential;
        // This gives you a Google Access Token. You can use it to access the Google API.
        var token = credential.accessToken;
        // The signed-in user info.
        var user = result.user;
        const usersRef = firebase.firestore().collection('Users');
        usersRef.doc(`${user.uid}`).set({
            FirstName: user.displayName,
            LastName: user.displayName,
            Email: user.email,
            UserRole: "Customer",
            RegisteredOn: new Date().toLocaleString() + ''
        });
    } catch (error) {
        // Handle Errors here.
        var errorCode = error.code;
        console.log(errorCode);
        var errorMessage = error.message;
        console.log(errorMessage);
        // The firebase.auth.AuthCredential type that was used.
        var credential_1 = error.credential;
        console.log(credential_1);
    }
}

export function logout() {
    
  return auth().signOut();
}