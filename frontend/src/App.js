import React, {Component} from 'react';
import './App.css';
import AppHeader from './AppHeader';
import AppFooter from './AppFooter';
import DayCard from './DayCard';

class App extends Component {



  render() {
    return (
      <div>
        <AppHeader/>
        <DayCard/>
        <AppFooter/>
      </div>
    )
  }
}

export default App;

