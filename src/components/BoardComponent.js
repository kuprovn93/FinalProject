import React from 'react';
import { ListItem, ListItemText,  CardMedia } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import {
  Link
} from 'react-router-dom'
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
const useStyles = makeStyles((theme) => ({
    root: {
      width: '100%',
      backgroundColor: theme.palette.background.paper,
    },
  }));


export default function BoardListItem({ key, board, boardImg }) {
    const classes = useStyles();
    
    
    return (
      <Card className={classes.root}>
      <CardActionArea>
        <CardMedia
          component="img"
          alt="Contemplative Reptile"
          height="140"
          image="public\logo192.png"
          title={board}
        />
        <CardContent>
          <Link to={`/boards/${ board }`}>SEE MORE</Link>
        </CardContent>
      </CardActionArea>

    </Card>
        
    );
}

