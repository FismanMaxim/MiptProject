let activeUser = new User();
let activeCompany = new Company();
let activeId = -1;

let isActiveCompany = false;

let companyList = new Map();
const url = 'http://localhost:4567/api';

function getAccountById() {
	if (isActiveCompany.flag) {
		
		fetch(url + '/company/' + activeId.id + '', {
			method: 'GET', 
			headers: {
				'Accept' : 'application/json',
				'Content-Type': 'application/json'
			},
		})
			.then(response => response.json())
			.then(data => setCompany(data))
			.catch(error => console.error(error));
	} else {
		
		console.log(activeId.id);
		fetch(url + '/usr/' + activeId.id + '', {
			method: 'GET', 
			headers: {
				'Accept' : 'application/json',
				'Content-Type': 'application/json'
			},
		})
			.then(response => response.json())
			.then(data => setUser(data))
			.catch(error => console.error(error));
	}
}

function setUser(user) {
	activeUser = user.user;
	if (activeUser != undefined) {
		activateAccount();
		closeForm();
		fillCompanyTable();
	}
}

function setCompany(company) {
	activeCompany = company.companyDTO;
	if (activeCompany != undefined) {
		activateAccount();
		closeForm();
		fillCompanyTable();
	}
}

function authorization() {
	let inputLogin;
	let inputPassword;
	let isCompany;
	
	try {
		inputLogin = document.querySelector(".lInputName").value;
		inputPassword = document.querySelector(".lInputPass").value;
		isCompany = document.querySelector(".lToggle").checked;
	} catch(error) {
		console.error(error);
		return;
	}
	
	if (isCompany) {
		const data = new URLSearchParams();
		data.append('name', inputLogin);
		data.append('password', inputPassword);
		
		if (!checkData(data)){
			selectInput()
			return;
		}
		
		const body = JSON.stringify({'name': inputLogin, 'password': inputPassword});
		
		fetch(url + '/company/auth', {
			method: 'POST', 
			headers: {
				'Accept' : 'application/json',
				'Content-Type': 'application/json'
			},
			body: body,
		})
			.then(response => response.json())
			.then(data => setId(data))
			.then(setIsCompany(true))
			.catch(error => console.error(error));
			
	} else {
		const data = new URLSearchParams();
		data.append('name', inputLogin);
		data.append('password', inputPassword);
		
		if (!checkData(data)){
			selectInput()
			return;
		}
		
		const body = JSON.stringify({'name': inputLogin, 'password': inputPassword});
		
		console.log(body);
		
		fetch(url + '/usr/auth', {
			method: 'POST', 
			headers: {
				'Accept' : 'application/json',
				'Content-Type': 'application/json'
			},
			body: body,
		})
			.then(response => response.json())
			.then(data => setId(data))
			.then(setIsCompany(false))
			.catch(error => console.error(error));
	}
}

function registration() {
	let inputLogin;
	let inputPassword;
	let isCompany;
	
	try {
		inputLogin = document.querySelector(".rInputName").value;
		inputPassword = document.querySelector(".rInputPass").value;
		isCompany = document.querySelector(".rToggle").checked;
	} catch(error) {
		console.error(error);
		return;
	}
	
	if (isCompany) {
		const data = new URLSearchParams();
		data.append('name', inputLogin);
		data.append('password', inputPassword);
		
		if (!checkData(data)){
			selectInput()
			return;
		}
		
		const body = JSON.stringify({'name': inputLogin, 'password':inputPassword});
		
		fetch(url + '/company', {
			method: 'POST', 
			headers: {
				'Accept' : 'application/json',
				'Content-Type': 'application/json'
			},
			body: body,
		})
			.then(response => response.json())
			.then(data => setId(data))
			.then(setIsCompany(true))
			.catch(error => console.error(error));
		
	} else {
		const data = new URLSearchParams();
		data.append("name", inputLogin);
		data.append("password", inputPassword);
		
		if (!checkData(data)){
			selectInput()
			return;
		}
		
		const body = JSON.stringify({'name': inputLogin, 'password':inputPassword});

		fetch(url + '/usr', {
			method: 'POST', 
			headers: {
				'Accept' : 'application/json',
				'Content-Type': 'application/json'
			},
			body: body,
		})
			.then(response => response.json())
			.then(data => setId(data))
			.then(setIsCompany(false))
			.catch(error => console.error(error));
	}
}

function setIsCompany(isCompany) {
	isActiveCompany = Object.freeze({
		flagValue : isCompany,
		get flag() {
			return this.flagValue;
		},
	});
}

function setId(id) {
	activeId = Object.freeze({
		idValue : id.id,
		get id() {
			return this.idValue;
		},
	});
	
	getAccountById();
}

function getCompanies() {	
	fetch(url + "/company", {
		method: 'GET',
		headers: {
			'Accept' : 'application/json',
			'Content-Type': 'application/json'
		}
	})
		.then(response => response.json())
		.then(data => setCompanyList(data))
		.catch(error => console.error(error));
}

function setCompanyList(list) {
	list = list.companiesResponses;
	
	companyList = new Map();
	for (let i = 0; i < list.length; i++){
		companyList.set(list[i].companyDTO.name, list[i].companyDTO);
	};
	
	fillCompanyTable();
}

async function buyShares() {
	let delta = 0;
	let companyId;
	let userId;
	
	try {
		delta = document.querySelector(".lInputSharesDelta").value;
		companyId = companyList.get(chosenCompany.companyName).id;
		userId = activeId.id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	const data = new URLSearchParams();
	data.append('sharesDelta', delta);
	data.append('companyId', companyId);
	
	if (!checkData(data)){
		selectInput()
		return;
	}
	
	console.log(companyList.get(chosenCompany.companyName));
	
	const body = JSON.stringify({'sharesDelta': [{'companyId':companyId, 'countDelta':delta}]});
	console.log(body);
	
	await fetch(url+"/usr/" + userId +"/shares", {
		method:"PUT", 
		headers: {
			'Accept' : 'application/json',
			'Content-Type': 'application/json'
		},
		body: body
	});
	getAccountById();
	
	let logText = "Buy " + delta + " shares of " + chosenCompany.companyName;
	createTransactionLog(logText);
	
	removeTransactionConfirmForm();
}

async function selShares() {
	let delta;
	let companyId;
	let userId;
	
	try {
		delta = -document.querySelector(".lInputSharesDelta").value;
		companyId = companyList.get(chosenCompany.companyName).id;
		userId = activeId.id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	const data = new URLSearchParams();
	data.append('sharesDelta', delta);
	data.append('companyId', companyId);
	
	if (!checkData(data)){
		selectInput()
		return;
	}
	
	const body = JSON.stringify({'sharesDelta': [{'companyId': companyId, 'countDelta':delta}]});
	
	await fetch(url+"/users/" + userId +"/shares", {
		method:"PUT", 
		headers: {
			'Accept' : 'application/json',
			'Content-Type': 'application/json'
		},
		body: body
	});
	getAccountById();
	
	let logText = "Sel " + delta + " shares of " + chosenCompany.companyName;
	createTransactionLog(logText);
	
	removeTransactionConfirmForm();
}

async function refillBalanceUser() {
	let delta;
	let userId;
	
	try {
		delta = document.querySelector(".lInputSharesDelta").value;
		userId = activeId.id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	const data = new URLSearchParams();
	data.append('deltaMoney', delta);
	
	if (!checkData(data)){
		selectInput()
		return;
	}
	
	const body = JSON.stringify({'deltaMoney': delta});
	
	await fetch(url+"/usr/" + userId, {
		method:"PUT", 
		headers: {
			'Accept' : 'application/json',
			'Content-Type': 'application/json'
		},
		body: body
	});
	getAccountById();
	
	let logText = "Top up balance for " + delta + " toncoins";
	createTransactionLog(logText);
	
	removeTransactionConfirmForm();
}

async function changeUserName() {
	let newName;
	let userId;
	
	try {
		newName = document.querySelector(".rInputSharesDelta").value;
		userId = activeId.id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	const data = new URLSearchParams();
	data.append('name', newName);
	
	if (!checkData(data)){
		selectInput()
		return;
	}
	
	const body = JSON.stringify({'name': newName});
	
	await fetch(url+"/usr/" + userId, {
		method:"PUT", 
		headers: {
			'Accept' : 'application/json',
			'Content-Type': 'application/json'
		},
		body: body
	});
	getAccountById();
	
	removeTransactionConfirmForm();
}

async function refillBalanceCompany() {
	let delta;
	let companyId;
	
	try {
		delta = document.querySelector(".lInputSharesDelta").value;
		companyId = activeId.id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	const data = new URLSearchParams();
	data.append('money', delta);
	
	if (!checkData(data)){
		selectInput()
		return;
	}
	
	const body = JSON.stringify({'money': delta});
	
	await fetch(url+"/company/" + companyId, {
		method:"PUT", 
		headers: {
			'Accept' : 'application/json',
			'Content-Type': 'application/json'
		},
		body: body
	});
	getAccountById();
	
	removeTransactionConfirmForm();
}

async function changeCompanyName() {
	let newName;
	let companyId;
	
	try {
		newName = document.querySelector(".rInputSharesDelta").value;
		companyId = activeId.id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	const data = new URLSearchParams();
	data.append('name', newName);
	
	if (!checkData(data)){
		selectInput()
		return;
	}
	
	const body = JSON.stringify({'name': newName});
	
	await fetch(url+"/company/" + companyId, {
		method:"PUT", 
		headers: {
			'Accept' : 'application/json',
			'Content-Type': 'application/json'
		},
		body: body
	});
	getAccountById();
	
	removeTransactionConfirmForm();
}

async function changeDeltaShares() {
	let delta;
	let companyId;
	
	try {
		delta = document.querySelector(".lInputSharesDelta").value;
		companyId = activeId.id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	const data = new URLSearchParams();
	data.append('deltaShares', delta);
	
	if (!checkData(data)){
		selectInput()
		return;
	}
	
	const body = JSON.stringify({'deltaShares': delta});
	
	await fetch(url+"/company/" + companyId, {
		method:"PUT", 
		headers: {
			'Accept' : 'application/json',
			'Content-Type': 'application/json'
		},
		body: body
	});
	getAccountById();
	
	let logText = "Change amount of shares on " + delta;
	createTransactionLog(logText);
	
	removeTransactionConfirmForm();
}

async function changeSharesCost() {
	let price;
	let companyId;
	
	try {
		delta = document.querySelector(".lInputSharesDelta").value;
		companyId = activeId.id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	const data = new URLSearchParams();
	data.append('sharePrice', price);
	
	if (!checkData(data)){
		selectInput()
		return;
	}
	
	const body = JSON.stringify({'sharePrice': price});
	
	await fetch(url+"/company/" + companyId, {
		method:"PUT", 
		headers: {
			'Accept' : 'application/json',
			'Content-Type': 'application/json'
		},
		body: body
	});
	getAccountById();
	
	let logText = "Change cost of shares on " + price;
	createTransactionLog(logText);
	
	removeTransactionConfirmForm();
}

async function changeThreshold() {
	let threshold;
	let companyId;
	
	try {
		threshold = document.querySelector(".lInputSharesDelta").value;
		companyId = activeId.id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	const data = new URLSearchParams();
	data.append('threshold', threshold);
	
	if (!checkData(data)){
		selectInput()
		return;
	}
	
	const body = JSON.stringify({'threshold': threshold});
	
	await fetch(url+"/company/" + companyId, {
		method:"PUT", 
		headers: {
			'Accept' : 'application/json',
			'Content-Type': 'application/json'
		},
		body: body
	});
	getAccountById();
	
	let logText = "Change threshold on " + delta;
	createTransactionLog(logText);
	
	removeTransactionConfirmForm();
}

function deleteUser() {
	let userId;
	
	try {
		userId = activeId.id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	fetch(url+"/usr/" + userId, {
		method:"DELETE", 
		headers: {
			'Accept' : 'application/json',
			'Content-Type': 'application/json'
		}
	});
	
	window.location.reload();
}

function deleteCompany() {
	let companyId;
	
	try {
		userId = activeId.id;
	} catch(error) {
		console.error(error);
		return;
	}
	
	fetch(url+"/company/" + companyId, {
		method:"DELETE", 
		headers: {
			'Accept' : 'application/json',
			'Content-Type': 'application/json'
		}
	});
	
	window.location.reload();
}

function createTransactionLog(text) {
	let el = document.createElement("div");
	el.className = "transactionLogBlock flexable";
	el.innerHTML = "<span>" + text + "</span>";
	document.querySelector(".transactionsLogHistory").appendChild(el);
}

function checkData(data) {
	let flag = true;
	data.forEach((value, key) => {
		if (!value) {
			flag = false;
		}
	});
	return flag;
}
