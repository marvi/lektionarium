import React, {Component} from 'react'
import PropTypes from 'prop-types';
import {Accordion, Icon} from 'semantic-ui-react'

class Readings extends Component {

  state = { activeIndex: 0 };

  handleClick = (e, titleProps) => {
    const { index } = titleProps;
    const { activeIndex } = this.state;
    const newIndex = activeIndex === index ? -1 : index;
    this.setState({ activeIndex: newIndex })
  };

  render() {
    const { activeIndex } = this.state;
    return (
      <Accordion>
        <Accordion.Title className="refHeader" active={activeIndex === 1} index={1} onClick={this.handleClick}>
          <Icon name='dropdown'/>
          {this.props.readings.ot.sweRef}
        </Accordion.Title>
        <Accordion.Content active={activeIndex === 1}>
          <div className="bibleText" dangerouslySetInnerHTML={this.createMarkup(this.props.readings.ot.text)} />
        </Accordion.Content>

        <Accordion.Title className="refHeader" active={activeIndex === 2} index={2} onClick={this.handleClick}>
          <Icon name='dropdown'/>
          {this.props.readings.ep.sweRef}
        </Accordion.Title>
        <Accordion.Content active={activeIndex === 2}>
          <div className="bibleText" dangerouslySetInnerHTML={this.createMarkup(this.props.readings.ep.text)} />
        </Accordion.Content>

        <Accordion.Title className="refHeader" active={activeIndex === 3} index={3} onClick={this.handleClick}>
          <Icon name='dropdown'/>
          {this.props.readings.go.sweRef}
        </Accordion.Title>
        <Accordion.Content active={activeIndex === 3}>
          <div className="bibleText" dangerouslySetInnerHTML={this.createMarkup(this.props.readings.go.text)} />
        </Accordion.Content>
        <Accordion.Title className="refHeader" active={activeIndex === 4} index={4} onClick={this.handleClick}>
          <Icon name='dropdown'/>
          {this.props.readings.ps.sweRef}
        </Accordion.Title>
        <Accordion.Content active={activeIndex === 4}>
          <div className="bibleText" dangerouslySetInnerHTML={this.createMarkup(this.props.readings.ps.text)} />
        </Accordion.Content>

      </Accordion>
    );
  };

  createMarkup(text) {
    return {__html: text};
  }


}

Readings.propTypes = {
  readings: PropTypes.object.isRequired
};

export default Readings;
