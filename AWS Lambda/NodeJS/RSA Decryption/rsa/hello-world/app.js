// const axios = require('axios')
// const url = 'http://checkip.amazonaws.com/';
let response;

/**
 *
 * Event doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format
 * @param {Object} event - API Gateway Lambda Proxy Input Format
 *
 * Context doc: https://docs.aws.amazon.com/lambda/latest/dg/nodejs-prog-model-context.html 
 * @param {Object} context
 *
 * Return doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html
 * @returns {Object} object - API Gateway Lambda Proxy Output Format
 * 
 */
exports.lambdaHandler = async (event, context) => {
    try {
        // const ret = await axios(url);

        console.log(event['queryStringParameters']['n']);
        console.log(event['queryStringParameters']['e']);
        console.log(event['queryStringParameters']['cipher']);

        // arguments n, e, cipher
        var n_ = event['queryStringParameters']['n'] || '';
        var e_ = event['queryStringParameters']['e'] || '';
        var cipher_ = event['queryStringParameters']['cipher'] || '';

        // cast parameters to BigInt
        var n = BigInt(n_);
        var e = BigInt(e_);
        var cipher = BigInt(cipher_);

        console.log("Public Keys");
        console.log("N: " + n);
        console.log("e: " + e);

        console.log("Cripted Message: " + cipher);

        var date = new Date();
        var start = date.getTime();

        var factorization = tdFactors(n);

        date = new Date();
        var end = date.getTime();
        console.log("Time for computing factorization: " + (end - start) + " ms");

        // check if N is a coprime number
        if (factorization.length != 2) {
            console.log("N is not coprime number!");
            return {
                'statusCode': 400,
                'body': JSON.stringify({
                    message: 'N is not coprime number!',
                    // location: ret.data.trim()
                })
            };
        }

        var p = factorization[0];
        var q = factorization[1];

        console.log("p: " + p + "\n" + "q: " + q);

        var phi = (p - BigInt('1')) * (q - BigInt('1'));

        console.log("phi: " + phi);

        //generate the d decryption exponent
        var d = inverse(e, phi);

        console.log("d: " + d);
        
        //var originalMessage = (cipher ** d)%n;

        //console.log("Original message " + originalMessage);
    
        var body = {
            'p': p.toString(),
            'q': q.toString(),
            'd': d.toString()
        }

        
        response = {
            'statusCode': 200,
            'body': body
        }

    } catch (err) {
        console.log(err);
        return err;
    }
    
    return response
};

//calculate multiplicative inverse of a%n using the extended euclidean GCD algorithm
function inverse(a, N) {

    var ans = extendedEuclid(a, N);

    if (ans[1] > BigInt('0')) return ans[1];
    else return ans[1] + N;
}

//Calculate d = gcd(a,N) = ax+yN
function extendedEuclid(a, N) {

    var ans = new Array(3);
    var ax, yN;

    if(N == BigInt('0')) {
        ans[0] = a;
        ans[1] = BigInt('1');
        ans[2] = BigInt('0');
        return ans;
    }

    ans = extendedEuclid(N, a%N);
    ax = ans[1];
    yN = ans[2];
    ans[1] = yN;
    var temp = a/N;
    temp = yN * temp;
    ans[2] = ax - temp;
    return ans;
}


// compute factorization
function tdFactors(n) {

    var fs = [];
    if(n < BigInt('2')) throw new IllegalArgumentException('arg n must be greater than one');

    while(n%BigInt('2') == 0) {
        fs.push(BigInt('2'));
        n /= BigInt('2');
    }

    if(n > BigInt('1')) {
        var f = BigInt('3');
        while((f*f) <= n) {
            if(n%f == 0) {
                fs.push(f);
                n /= f;
            }
            else {
                f += BigInt('2');
            }
        }
        fs.push(n);
    }
    return fs;
}

class IllegalArgumentException extends Error {
    constructor(message) {
      super(message);
      this.name = 'IllegalArgumentException';
    }
  }
