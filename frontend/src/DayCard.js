import React, {Component} from 'react';
import {Button, Header, Divider, Container} from 'semantic-ui-react';
import axios from 'axios';
import Readings from './Readings';


class DayCard extends Component {


  constructor(props) {
    super(props);
    this.state =
      {
        errors: '',
        day: {
          "day": "",
          "date": "",
          "readings": {
            "theme": "",
            "ot": {"text": "", "sweRef": "", "enRef": "", "readingType": "OT"},
            "ep": {"text": "", "sweRef": "", "enRef": "", "readingType": "EP"},
            "go": {"text": "", "sweRef": "", "enRef": "", "readingType": "GO"},
            "ps": {"text": "", "sweRef": "", "enRef": "", "readingType": "PS"},
            "alt": null
          }
        }
      };
    this.handleNext = this.handleNext.bind(this);
    this.handlePrevious = this.handlePrevious.bind(this);
  }


  componentDidMount() {
    axios.get('/day')
      .then((response) => {
        const day = response.data;
        //console.log(day);
        this.setState({day: day});
      })
      .catch((error) => {
        this.setState({errors: this.state.errors.concat(['GET Request Failed'])});
      });
  }

  handleNext() {
    axios.get('/next/' + this.state.day.date)
      .then((response) => {
        const day = response.data;
        //console.log(day);
        this.setState({day: day});
      })
      .catch((error) => {
        this.setState({errors: this.state.errors.concat(['GET Request Failed'])});
      });
  }

  handlePrevious() {
    axios.get('/previous/' + this.state.day.date)
      .then((response) => {
        const day = response.data;
        //console.log(day);
        this.setState({day: day});
      })
      .catch((error) => {
        this.setState({errors: this.state.errors.concat(['GET Request Failed'])});
      });
  }

  render() {
    const hasReadings = this.state.day.readings;
    let readings;
    if(hasReadings) {
      readings = <div>
        <Header className="lectioHeader" as='h3'>{this.state.day.readings.theme}</Header>
        <Readings readings={this.state.day.readings} />
      </div>;
    }
    else {
      readings = <div></div>;
    }

    return (
      <Container style={{marginTop: '6em', maxWidth: '500px', textAlign: 'left'}}>

        <Header className="lectioHeader" as='h3'>{this.state.day.date}</Header>
        <Header className="lectioHeader" as='h1'>{this.state.day.day}</Header>
        {readings}
        <Divider/>
        <Container>
          <Button onClick={this.handlePrevious} primary icon='left arrow' labelPosition='left' content="Förra"/>
          <Button onClick={this.handleNext} primary icon='right arrow' labelPosition='right' content="Nästa"/>
        </Container>
      </Container>
    );
  };
}

export default DayCard;
