function sendGreeting() {
    const name = document.getElementById("name").value || "World";

    fetch(`/app/greeting?name=${encodeURIComponent(name)}`)
        .then(response => response.text())
        .then(data => {
            document.getElementById("responseGet").innerText = data;
        })
        .catch(error => {
            document.getElementById("responseGet").innerText = "Error de conexión";
            console.error(error);
        });
}

function checkParity() {
    const number = document.getElementById("number").value;

    fetch(`/app/parity?number=${encodeURIComponent(number)}`)
        .then(response => response.text())
        .then(data => {
            document.getElementById("responsePost").innerText = data;
        })
        .catch(error => {
            document.getElementById("responsePost").innerText = "Error de conexión";
            console.error(error);
        });
}