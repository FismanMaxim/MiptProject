function chooseReviewTab(className) {
	document.querySelector("#activeReviewBlock").id = "";
	document.querySelector("#activeReviewButton").id = "";
	
	document.querySelector("." + className + "Button").id = "activeReviewButton";
	document.querySelector("." + className + "Block").id = "activeReviewBlock";
	
}

let isFormOpened = 0;
function loginForm() {
	document.querySelector(".loginForm").style.display = "block";
	document.querySelector(".registerForm").style.display = "none";
	
	if (isFormOpened != 1) {
		isFormOpened = 1;
		requestAnimationFrame(openForm);
	} else {
		isFormOpened = 0;
		requestAnimationFrame(closeForm);
	}
}
function registerForm() {
	document.querySelector(".registerForm").style.display = "block";
	document.querySelector(".loginForm").style.display = "none";
		
	if (isFormOpened != 2) {
		isFormOpened = 2;
		requestAnimationFrame(openForm);
	} else {
		isFormOpened = 0;
		requestAnimationFrame(closeForm);
	}
}

function openForm() {
	document.querySelector(".registerBlock").style.display = "block";
	document.querySelector(".main").style.filter = "blur(4px)";
	document.querySelector(".rdc").style.display = "block";
	document.querySelector(".ldc").style.display = "block";
}
function closeForm() {
	document.querySelector(".registerBlock").style.display = "none";
	document.querySelector(".main").style.filter = "blur(0px)";
}

function editShares(name, cost) {
	document.querySelector(".companyName").innerHTML = "Shareholder: " + name;
	document.querySelector(".companyCost").innerHTML = "The share price is " + cost;
	document.querySelector(".transactionBlock").style.display = "flex";
}
function removeTransitionForm() {
	document.querySelector(".transactionBlock").style.display = "none";
}


const url = 'http://myIp.com';
var userid;
function buyShares(companyId, value) {
	fetch(url+"/usr/"+userid+"/shares", {
		method:"POST", 
		headers: {
			'Content-Type': 'application/json;charset=utf-8'
		},
		body: "{sharesDelta : [{\"companyId : "+companyId+"\ "+value+"}"
	})
}

function prepairPage() {
	
}

function getShares() {
	
}
