## App Base : Auth
[![Build Status](https://travis-ci.org/JFL110/app-base-auth.svg?branch=master)](https://travis-ci.org/JFL110/app-base-auth) [![Coverage Status](https://coveralls.io/repos/github/JFL110/app-base-auth/badge.svg)](https://coveralls.io/github/JFL110/app-base-auth)

Module to add authentication fundamentals to a web application 

__Not yet production ready__

This module provides utilities to add a number of authentication strategies. A consuming application will need to provide some implementations, for example persistence services.

### Strategies 

#### Rolling Key
Rolling key is designed to eliminate the need for session management, enabling services to be truly stateless. The server generates a number of keys that are each valid for a small period of time (e.g. 1 minute). Together the keys cover a block of time which spans some moments in the past and future. New keys are periodically added and old ones are tail dropped. User auth tokens are hashed using the key which is valid at that moment. When validating a token, the server looks up the key for the issue time and checks the hash of the token. Because the number of keys is small and doesn't change for a given timespan they can be held in memory, so a database hit isn't needed to validate a token.
