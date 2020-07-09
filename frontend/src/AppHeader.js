import React from 'react';
import {Container, Dropdown, Image, Menu} from 'semantic-ui-react';

const AppHeader = () => (
  <Menu fixed='top' inverted>
    <Container>
      <Menu.Item as='a' header>
        <Image
          size='mini'
          src='/logo_notext.svg'
          style={{marginRight: '1.5em'}}
        />
        Lektionarium
      </Menu.Item>
      <Dropdown item simple text='Ladda hem'>
        <Dropdown.Menu>
          <Dropdown.Item as='a' href='/ical/2020' download='lectio-2020.ics' target='_none'>2020</Dropdown.Item>
          <Dropdown.Item as='a' href='/ical/2021' download='lectio-2021.ics' target='_none'>2021</Dropdown.Item>
          <Dropdown.Item as='a' href='/ical/2022' download='lectio-2022.ics' target='_none'>2022</Dropdown.Item>
          <Dropdown.Item as='a' href='/ical/2023' download='lectio-2023.ics' target='_none'>2023</Dropdown.Item>
          <Dropdown.Item as='a' href='/ical/2024' download='lectio-2024.ics' target='_none'>2024</Dropdown.Item>
          <Dropdown.Item as='a' href='/ical/2025' download='lectio-2025.ics' target='_none'>2025</Dropdown.Item>
          <Dropdown.Item as='a' href='/ical/2026' download='lectio-2026.ics' target='_none'>2026</Dropdown.Item>
        </Dropdown.Menu>
      </Dropdown>
    </Container>
  </Menu>
);

export default AppHeader;
