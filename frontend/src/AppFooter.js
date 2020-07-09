import React from 'react'
import {Container, Image, Segment, Grid, List} from 'semantic-ui-react'

const AppFooter = () => (

  <Segment
    inverted
    vertical
    style={{margin: '5em 0em 0em', padding: '5em 0em'}}
  >
    <Container textAlign='center'>
      <Grid divided inverted stackable>
        <Grid.Column width={6}>
          <Image
              centered
              size='small'
              src='/logo_transparent.svg'
          />
        </Grid.Column>
        <Grid.Column width={6} textAlign='right'>
          <List link inverted>
            <List.Item as='a' href='https://marvi.io'>Gjort med &#9829; av marvi.io</List.Item>
            <List.Item as='a' href='https://github.com/marvi/lektionarium'><i className="github icon"></i> Källkod och buggrapporter</List.Item>
            <List.Item as='a' href='https://bibeln.se'>Bibeltext från Bibel 2000. &#169; Svenska Bibelsällskapet</List.Item>
          </List>
        </Grid.Column>
      </Grid>
    </Container>
  </Segment>
)

export default AppFooter;
