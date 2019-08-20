import _ from 'lodash/fp';
import React from 'react';
import './App.css';
import db from './db';
import ds from 'datascript';

function App() {
  return (
    <div>
      <HotelsByDistrict />
      <RoomOffers />
      <RoomCategories />
    </div>
  );
}

function HotelsByDistrict() {
  const districts = _.map(([id, name]) => ({ id, name }), ds.q(`
    [:find ?district ?name
     :where [?district "is kind of" "district"]
            [?district "has name" ?name]]    
  `, db))


  return (
    <Column represents="hotels">
      {_.map((district) => {

        const hotels = _.map(([id, name]) => ({ id, name }), ds.q(`
          [:find ?hotel ?name
           :where [?hotel "is kind of" "hotel"]
                  [?hotel "has name" ?name]
                  [?hotel "is in district" "${district.id}"]]
        `, db))

        return (
          <Column represents={district.id}>
            <Box represents={`${district.id}.name`} style={{ background: 'blue', color: '#fff' }}>
              {district.name}
            </Box>

            {
              _.map((hotel) => (
                <Box represents={hotel.id}>
                  {hotel.name}
                </Box>
              ), hotels)
            }
          </Column>
        )
      }, districts)}
    </Column>
  )
}


function RoomOffers() {
  const cateogories = _.map(([id]) => ({ id }), ds.q(`
    [:find ?category
     :where [?category "is kind of" "category"]]
  `, db))

  return (
    <Row represents="roomOffers">
      {_.map((category) => {
        const roomOffers = _.map(([id, price]) => ({ id, price }), ds.q(`
        [:find ?offer ?price 
         :where [?offer "is kind of" "room offer"]
                [?offer "has price" ?price]
                [?offer "has category" "${category.id}"]]
      `, db))

        return (
          <Column represents={category.id}>
            {_.map((roomOffer) => <Box represents={roomOffer.id}>$ {roomOffer.price}</Box>, roomOffers)}
          </Column>
        );
      }, cateogories)}
    </Row>
  )
}

function RoomCategories() {
  const categories = _.map(([id, name, description]) => ({ id, name, description }), ds.q(`
    [:find ?category ?name ?description
    :where [?category "is kind of" "category"]
           [?category "has name" ?name]
           [?category "has description" ?description]]
  `, db))

  return (
    <Column represents="category">
      {_.map((category) => {
        return (
          <Row represents={category.id}>
            <Box represents={`${category}.name`}>{category.name}</Box>
            <Box represents={`${category}.decription`}>{category.description}</Box>
          </Row>
        )
      }, categories)}
    </Column>
  )
}

// placeholder

function Column({ children, style, represents }) {
  return (
    <layout-column
      style={style}
      data-represents={represents}
    >
      {children}
    </layout-column>
  )
}

function Row({ children, style, represents }) {
  return (
    <layout-row
      style={style}
      data-represents={represents}
    >
      {children}
    </layout-row>
  )
}

function Box({ children, style, represents }) {
  return (
    <layout-box
      style={style}
      represents={represents}
    >
      {children}
    </layout-box>
  );
}

export default App;
