function sleep(s){
    return new Promise(function(resolve,reject) {
        setTimeout(_ => {
         resolve();
        }, 1000 * s);    
    });
}

async function start(){
    while(true){
        console.log("while start")
        await sleep(2);
    }

}

start();

