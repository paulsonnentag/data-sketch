import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import layoutEngine from '../src/layoutEngine';

const root = document.getElementById('root')


layoutEngine.mount(root);

ReactDOM.render(<App />, root);
