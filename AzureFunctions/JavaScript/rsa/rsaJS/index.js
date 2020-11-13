/**
 * RSA Decryption Algorithm. Javascript version.
 */

module.exports = async function (context, req) {

    context.log('RSA Decryption Algorithm');

    // arguments n, e, cipher
    const n_ = req.query.n || '';
    const e_ = req.query.e || '';
    const cipher_ = req.query.cipher || '';

    console.log("E: " + e_);

    if (!n_) {
        console.log("The private key n is not defined!");
        return context.res = {
            status: 400,
            body: {
                'Error': 'The private key n is not defined!'
            }
        };
    }

    if (!e_) {
        console.log("The private key e is not defined!");
        return context.res = {
            status: 400,
            body: {
                'Error': 'The private key e is not defined!'
            }
        };
    }

    if (!cipher_) {
        console.log("The cipher is not defined!");
        return context.res = {
            status: 400,
            body: {
                'Error': 'The cipher is not defined!'
            }
        };
    }

    // cast parameters to BigInt
    var n = BigInt(n_);
    var e = BigInt(e_);
    var cipher = BigInt(cipher_);

    console.log("Public Keys");
    console.log("N: " + n);
    console.log("e: " + e);

    console.log("Cripted Message: " + cipher);

    var d = new Date();
    var start = d.getTime();

    var factorization = tdFactors(n);

    d = new Date();
    var end = d.getTime();
    console.log("Time for computing factorization: " + (end - start) + " ms");

    // check if N is a coprime number
    if (factorization.length != 2) {
        console.log("N is not coprime number!");
        context.res = {
            status: 400,
            body: {
                'Error': 'N is not coprime number!'
            }
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

    context.res = {
        status: 200,
        body: {
            "p" : p.toString(),
            "q" : q.toString(),
            "d" : d.toString()
        }
    };
}

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