import React from 'react'
import {Container, Image, Segment} from 'semantic-ui-react'

const AppFooter = () => (

  <Segment
    inverted
    vertical
    style={{margin: '5em 0em 0em', padding: '5em 0em'}}
  >
    <Container textAlign='center'>
      <Image
        centered
        size='small'
        src='/logo_transparent.svg'
      />
    </Container>
  </Segment>
)

export default AppFooter;
