import ds from 'datascript';
import _ from 'lodash/fp';

const singleCategory = _.uniqueId('category');
const doubleCategory = _.uniqueId('category');
const honeyMoonSuiteCategory = _.uniqueId('category');
const presidentSuiteCategory = _.uniqueId('category');

const facts =
    _.flatten([
        // CATEGORIES
        getCategory(singleCategory, {
            name: 'single bed',
            description: 'just a single bed for one person',
        }),
        getCategory(doubleCategory, {
            name: 'double bed',
            description: 'one double bed for up to two people',
        }),
        getCategory(honeyMoonSuiteCategory, {
            name: 'Honey Moon suite',
            description: 'for a very special occasion',
        }),
        getCategory(presidentSuiteCategory, {
            name: 'President Suite',
            description: 'if you just don\'t care about money',
        }),

        // DISTRICTS
        getDistrictWithRandomHotels({ name: 'Downtown' }),
        getDistrictWithRandomHotels({ name: 'East Side' }),
        getDistrictWithRandomHotels({ name: 'West Side' }),
    ])

const db = ds.init_db(facts)

function getCategory(category, { name, description }) {
    return [
        [category, 'is kind of', 'category'],
        [category, 'has name', name],
        [category, 'has description', description]
    ]
}

function getDistrictWithRandomHotels({ name }) {
    const district = _.uniqueId('district')

    return [
        [district, 'is kind of', 'district'],
        [district, 'has name', name],
    ].concat(
        getRandomHotels({ district, numberOfHotels: _.random(3, 5) })
    )
}

function getRandomHotels({ district, numberOfHotels }) {
    return _.flatten(
        _.times(() => {
            const hotel = _.uniqueId('hotel');

            const basePrice = _.random(50, 300)
            const factor = _.random(1.2, 1.5)

            return ([
                [hotel, 'has name', randomHotelName(basePrice)],
                [hotel, 'is kind of', 'hotel'],
                [hotel, 'is in district', district],
            ]
                .concat(
                    getRoomOffer({ hotel, category: singleCategory, price: basePrice })
                )
                .concat(
                    getRoomOffer({ hotel, category: doubleCategory, price: Math.round(basePrice * factor) }),
                )
                .concat(
                    (basePrice > 100) ?
                        getRoomOffer({ hotel, category: honeyMoonSuiteCategory, price: Math.round(basePrice * Math.pow(factor, 2)) }) : []
                )
                .concat(
                    (basePrice > 200) ?
                        getRoomOffer({ hotel, category: presidentSuiteCategory, price: Math.round(basePrice * Math.pow(factor, 3)) }) : []
                )
            )
        }, numberOfHotels)
    )
}

function randomHotelName(basePrice) {
    const adjectives = ['North', 'South', 'West', 'East', 'Royal', 'Best', 'Blue', 'Golden'];
    const things = ['Castle', 'Garden', 'Ocean', 'Village'];
    const kinds = basePrice > 100 ? ['Hotel', 'Resort'] : ['Hostel', 'Motel'];

    return [
        adjectives[_.random(0, adjectives.length - 1)],
        things[_.random(0, things.length - 1)],
        kinds[_.random(0, kinds.length - 1)]
    ].join(' ');
}

function getRoomOffer({ hotel, category, price }) {
    const roomOffer = _.uniqueId('roomOffer')

    return [
        [hotel, 'offers', roomOffer],
        [roomOffer, 'is kind of', 'room offer'],
        [roomOffer, 'has category', category],
        [roomOffer, 'has price', price]
    ]
}

export default db;