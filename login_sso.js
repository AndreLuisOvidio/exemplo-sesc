const crypto = require('crypto');

const token = '2884ecbb1b459ef5df2205f8c6687916eef22cf11d029c3753bca9a8510359f0';
const usuarioId = '16978';

const auth = criptografaAES(usuarioId, token);

const unixTime = Math.floor(Date.now() / 1000);
console.log(unixTime);
const tokenCripto = criptografaAES(unixTime.toString(), token);

const url = `https://sescms.mentorweb.ws/sescmsMWFlutterWeb/#/loginSso?auth=${auth}&token=${tokenCripto}`;
console.log(url);


function criptografaAES(valor, chave) {
    try {
        const senha = geraHash(chave, 'md5');
        const paddedValue = nullPadString(valor);
        const encrypted = encode(paddedValue, senha);
        return fromHex(encrypted);
    } catch (error) {
        console.error(error);
        return '';
    }
}

function geraHash(senha, algoritmo) {
    return crypto.createHash(algoritmo).update(senha).digest();
}

function fromHex(buffer) {
    return buffer.toString('hex');
}

function nullPadString(str) {
    const buffer = Buffer.from(str, 'utf8');
    const padding = Buffer.alloc(16 - (buffer.length % 16), 0);
    return Buffer.concat([buffer, padding]);
}

function encode(input, key) {
    const cipher = crypto.createCipheriv('aes-128-ecb', key, null);
    cipher.setAutoPadding(false);
    return Buffer.concat([cipher.update(input), cipher.final()]);
}

function decode(input, key) {
    const decipher = crypto.createDecipheriv('aes-128-ecb', key, null);
    decipher.setAutoPadding(false);
    return Buffer.concat([decipher.update(input), decipher.final()]).toString('utf8').replace(/\0/g, '');
}


